package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;

public class CustomAuthenticationBroker extends AbstractAuthenticationBroker {

    public CustomAuthenticationBroker(Broker next) {
        super(next);
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        SecurityContext securityContext = context.getSecurityContext();
        if (securityContext == null) {
            securityContext = authenticate(info.getUserName(), info.getPassword(), null);
            context.setSecurityContext(securityContext);
            securityContexts.add(securityContext);
        }

        try {
            super.addConnection(context, info);
        } catch (Exception e) {
            securityContexts.remove(securityContext);
            context.setSecurityContext(null);
            throw e;
        }
    }

    @Override
    public SecurityContext authenticate(String username, String password, X509Certificate[] x509Certificates) throws SecurityException {
        System.out.println("Authenticating user '"+username+"' with password '"+password+"'");

        return new SecurityContext(username) {
            @Override
            public Set<Principal> getPrincipals() {
                Principal p = new Principal() {
                    @Override
                    public String getName() {
                        return "custom principal";
                    }
                };
                return Collections.singleton(p);
            }
        };
    }
}
