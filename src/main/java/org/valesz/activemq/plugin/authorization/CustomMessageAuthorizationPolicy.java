package org.valesz.activemq.plugin.authorization;

import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.Message;
import org.apache.activemq.security.MessageAuthorizationPolicy;
import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.util.ByteSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valesz.activemq.data.AuthorizedMessage;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;

public class CustomMessageAuthorizationPolicy implements MessageAuthorizationPolicy {

    private static final Logger LOG = LoggerFactory.getLogger(CustomMessageAuthorizationPolicy.class);

    /**
     * Intercept only messages with this property set to 'AuthorizedMessage'.
     */
    public static final String MESSAGE_TYPE = "messageType";

    @Override
    public boolean isAllowedToConsume(ConnectionContext connectionContext, Message message) {

        dumpInfo(connectionContext, message);
        boolean res = true;

        if(isAuthorizedMessage(message)) {
            LOG.info("Authorized message received.");
            if (message instanceof BytesMessage) {
                res = handleBytesAuthorizedMessage(connectionContext, (BytesMessage)message);
            }

            // other message types...
        }

        LOG.trace("====================================");


        return res;
    }

    private boolean handleBytesAuthorizedMessage(ConnectionContext connectionContext, BytesMessage message) {
        LOG.info("Handling authorized message sent as bytes.");

        try {
            AuthorizedMessage am = new AuthorizedMessage(0, "", Arrays.asList(message.getStringProperty("authorizedClients").split(";")));
            return handleAuthorizedMessage(connectionContext, am);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAuthorizedMessage(Message message) {
        try {
            return message.getProperties().containsKey(MESSAGE_TYPE) &&
                    AuthorizedMessage.class.getSimpleName().equals(message.getProperties().get(MESSAGE_TYPE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void dumpInfo(ConnectionContext connectionContext, Message message) {
        LOG.trace("====================================");
        LOG.trace("Custom message authorization plugin:");
        LOG.trace("Message type: {}", message.getClass());
        LOG.trace("Message: {}", message);
        ByteSequence content = message.getContent();
        LOG.trace("Message content length: {}", content.length);
        LOG.trace("Message content raw data: {}", content.data);
        LOG.trace("Message content string raw data: {}", new String(content.data));

        LOG.trace("Message properties:");
        try {
            message.getProperties().forEach((k,v) -> LOG.trace("\t{}:{}", k,v));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.trace("Client id: {}", connectionContext.getClientId());
        LOG.trace("Client username: {} ", connectionContext.getUserName());
        SecurityContext sc = connectionContext.getSecurityContext();
        LOG.trace("Security context: {}", sc);
        if (sc != null) {
            LOG.trace("Principals: {}", sc.getPrincipals());
            for (Principal p : sc.getPrincipals()) {
                LOG.trace("\tPrincipal name: {}", p.getName());
                LOG.trace("\tPrincipal class: {}", p.getClass());
            }
        }
    }

    private boolean handleAuthorizedMessage(ConnectionContext connectionContext, AuthorizedMessage authorizedMessage) {
        String username = connectionContext.getUserName();
        LOG.info("Authorizing message for client {}.", username);

        if (authorizedMessage == null) {
            LOG.warn("No authorization message.");
            return false;
        }

        LOG.debug("Clients authorized to read the message: {}", authorizedMessage.getAuthorizedClients());
        if (authorizedMessage.getAuthorizedClients().contains(username)) {
            LOG.info("Client {} is authorized to read the message.", username);
            return true;
        } else {
            LOG.info("Client {} is not authorized to read the message.", username);
            return false;
        }
    }
}
