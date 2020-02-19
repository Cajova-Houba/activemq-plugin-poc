package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.security.SecurityContext;
import org.junit.Test;

import java.security.Principal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class CustomAuthenticationBrokerTest {

    /**
     * Check that everything works and no exception is thrown.
     */
    @Test
    public void testAuthenticate() {

        CustomAuthenticationBroker broker = new CustomAuthenticationBroker(null);
        SecurityContext sc = broker.authenticate("", "", null);

        assertNotNull("Null security context!", sc);
        assertFalse("No principals!", sc.getPrincipals().isEmpty());

        CustomPrincipal cp = null;
        for (Principal p : sc.getPrincipals()) {
            if (p instanceof CustomPrincipal) {
                cp = (CustomPrincipal) p;
                break;
            }
        }

        assertNotNull("Custom principal not included!", cp);
        assertFalse("Custom principal value is empty!", cp.getValue().isEmpty());
        System.out.println(cp.getValue());
    }
}
