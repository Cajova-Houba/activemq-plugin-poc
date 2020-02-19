package org.valesz.activemq.plugin.authorization;

import org.apache.activemq.ActiveMQMessageTransformation;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.Message;
import org.apache.activemq.network.jms.SimpleJmsMessageConvertor;
import org.apache.activemq.security.MessageAuthorizationPolicy;
import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.util.ByteSequence;
import org.valesz.activemq.data.AuthorizedMessage;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.IOException;
import java.security.Principal;

public class CustomMessageAuthorizationPolicy implements MessageAuthorizationPolicy {

    @Override
    public boolean isAllowedToConsume(ConnectionContext connectionContext, Message message) {

        System.out.println("====================================");
        System.out.println("Custom message authorization plugin:");
        System.out.println("Message type: "+message.getClass());
        System.out.println("Message: "+message);
        ByteSequence content = message.getContent();
        System.out.println("Message content length: "+content.length);
        System.out.println("Message content raw data: "+content.data);
        System.out.println("JMSX Mime Type: " + ((ActiveMQBytesMessage)message).getJMSXMimeType());
        System.out.println("Client id: "+connectionContext.getClientId());
        System.out.println("Client username: "+connectionContext.getUserName());
        SecurityContext sc = connectionContext.getSecurityContext();
        System.out.println("Security context: "+sc);
        if (sc != null) {
            System.out.println("Principals: "+sc.getPrincipals());
            for (Principal p : sc.getPrincipals()) {
                System.out.println("\tPrincipal name: "+p.getName());
                System.out.println("\tPrincipal class: "+p.getClass());
            }
        }

        try {
            message.getProperties().forEach((k,v) -> System.out.println(k+": "+v));
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Is ActiveMQObjectMessage: " + (message instanceof ActiveMQObjectMessage));

        AuthorizedMessage msg = AuthorizedMessage.fromByteSequence(content.data);
        if (msg != null) {
            handleAuthorizedMessage(connectionContext, msg);
        }
        System.out.println("====================================");


        return true;
    }

    private void handleAuthorizedMessage(ConnectionContext connectionContext, AuthorizedMessage authorizedMessage) {
        System.out.println("Authorizing authorized message.");
        System.out.println("Client id: "+connectionContext.getClientId());
        System.out.println("Clients authorized to read the message: "+authorizedMessage.getAuthorizedClients());
    }
}
