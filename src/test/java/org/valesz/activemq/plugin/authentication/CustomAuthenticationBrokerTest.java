package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.security.SecurityContext;
import org.junit.Test;
import org.valesz.activemq.service.membernet.MembernetService;

import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class CustomAuthenticationBrokerTest {

    private CustomAuthenticationBrokerConfiguration configuration = new CustomAuthenticationBrokerConfiguration("u", "p");

    /**
     * Check that everything works and no exception is thrown.
     */
    @Test
    public void testAuthenticate() {

        CustomAuthenticationBroker broker = new CustomAuthenticationBroker(null, configuration, new MembernetService() {
            @Override
            public boolean canReadDestination(String destination, String accessToken) {
                return true;
            }

            @Override
            public boolean authenticate(String username, String accessToken) {
                return true;
            }
        });

        final String username = "username";
        final String accessToken = "accessToken";

        SecurityContext sc = broker.authenticate(username, accessToken, null);

        assertNotNull("Null security context!", sc);
        assertFalse("No principals!", sc.getPrincipals().isEmpty());

        CustomPrincipal cp = null;
        for (Principal p : sc.getPrincipals()) {
            if (p instanceof CustomPrincipal) {
                cp = (CustomPrincipal) p;
                break;
            }
        }

        assertNotNull("No principal with access token!", cp);
        assertEquals("Wrong access token set as principal!", accessToken, cp.getValue());
    }
}
