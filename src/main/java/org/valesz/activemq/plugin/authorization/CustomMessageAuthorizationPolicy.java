package org.valesz.activemq.plugin.authorization;

import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.Message;
import org.apache.activemq.security.MessageAuthorizationPolicy;
import org.apache.activemq.security.SecurityContext;
import org.valesz.activemq.data.AuthorizedMessage;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.security.Principal;

public class CustomMessageAuthorizationPolicy implements MessageAuthorizationPolicy {

    @Override
    public boolean isAllowedToConsume(ConnectionContext connectionContext, Message message) {

        System.out.println("====================================");
        System.out.println("Custom message authorization plugin:");
        System.out.println("Message: "+message);
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
            if (message instanceof ObjectMessage && ((ObjectMessage) message).getObject() instanceof AuthorizedMessage) {
                handleAuthorizedMessage(connectionContext, (AuthorizedMessage) ((ObjectMessage) message).getObject());
            }
        } catch (JMSException e) {
            e.printStackTrace();
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
