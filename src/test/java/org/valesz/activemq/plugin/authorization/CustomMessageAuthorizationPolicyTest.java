package org.valesz.activemq.plugin.authorization;

import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.security.SecurityContext;
import org.junit.Test;
import org.valesz.activemq.data.AuthorizedMessage;

import javax.jms.JMSException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomMessageAuthorizationPolicyTest {

    @Test
    public void testAuthorizeTextByteMessage_authorized() throws JMSException {
        AuthorizedMessage am = new AuthorizedMessage(1, "text", Arrays.asList("c1", "c2"));
        ActiveMQBytesMessage abm = asTextToByteMessage(am);

        ConnectionContext goodCon = prepareConnectionContext("c1");

        CustomMessageAuthorizationPolicy messageAuthorizationPolicy = new CustomMessageAuthorizationPolicy();
        assertTrue("Client 1 should be able to access the message!", messageAuthorizationPolicy.isAllowedToConsume(goodCon, abm));
    }

    @Test
    public void testAuthorizeTextByteMessage_notAuthorized() throws JMSException {
        AuthorizedMessage am = new AuthorizedMessage(1, "text", Arrays.asList("c1", "c2"));
        ActiveMQBytesMessage abm = asTextToByteMessage(am);

        ConnectionContext badCon = prepareConnectionContext("c5");

        CustomMessageAuthorizationPolicy messageAuthorizationPolicy = new CustomMessageAuthorizationPolicy();
        assertFalse("Client 5 should not be able to access the message!", messageAuthorizationPolicy.isAllowedToConsume(badCon, abm));
    }

    private ConnectionContext prepareConnectionContext(String username) {
        SecurityContext sc = prepareSecurityContext(username);
        ConnectionContext cc = new ConnectionContext();

        cc.setSecurityContext(sc);
        cc.setClientId(username);

        return cc;
    }

    private SecurityContext prepareSecurityContext(String username) {
        SecurityContext sc = new SecurityContext(username) {
            @Override
            public Set<Principal> getPrincipals() {
                return Collections.emptySet();
            }
        };

        return sc;
    }

    private ActiveMQBytesMessage asTextToByteMessage(AuthorizedMessage am) throws JMSException {
        ActiveMQBytesMessage abm = new ActiveMQBytesMessage();
        abm.setStringProperty("messageType", AuthorizedMessage.class.getSimpleName());
        abm.setStringProperty("authorizedClients", am.getClientsAsString());
        abm.writeObject(am.getText());
        abm.reset();
        return abm;
    }
}
