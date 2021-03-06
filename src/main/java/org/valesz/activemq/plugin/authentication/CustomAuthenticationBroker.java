package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valesz.activemq.Utils;
import org.valesz.activemq.service.membernet.MembernetService;
import org.valesz.activemq.service.tronalddump.TronaldDumpService;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.*;

public class CustomAuthenticationBroker extends AbstractAuthenticationBroker {

    private static final Logger LOG = LoggerFactory.getLogger(CustomAuthenticationBroker.class);

    private CustomAuthenticationBrokerConfiguration configuration;

    private MembernetService membernetService;

    public CustomAuthenticationBroker(Broker next, CustomAuthenticationBrokerConfiguration configuration, MembernetService membernetService) {
        super(next);
        this.configuration = configuration;
        this.membernetService = membernetService;
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

    /**
     * Client authentication.
     *
     * @param username MN username.
     * @param password OAuth2 access token. Principal with this as a value will be added to security context if the authentication
     *                 is successful.
     * @param x509Certificates Not required.
     * @return
     * @throws SecurityException
     */
    @Override
    public SecurityContext authenticate(String username, String password, X509Certificate[] x509Certificates) throws SecurityException {
        LOG.info("Authenticating user '{}'", username);

        LOG.trace("Calling authentication API");

        // check if the user being authenticated is publisher or not
        if (authenticatePublisher(username, password)) {
            LOG.info("User '{}' authenticated as publisher.", username);
            return new SecurityContext(username) {
                @Override
                public Set<Principal> getPrincipals() {
                    Principal groupPrincipal = new GroupPrincipal("admins");
                    return Collections.singleton(groupPrincipal);
                }
            };


        // if not publisher, use MN to authenticate him
        } else if (membernetService.authenticate(username, password)) {
            return new SecurityContext(username) {
                @Override
                public Set<Principal> getPrincipals() {
                    Principal p = new CustomPrincipal(password);
                    Principal group = new GroupPrincipal("users");

                    return new HashSet<>(Arrays.asList(p, group));
                }
            };
        } else {
            throw new SecurityException("Authentication failed for [" + username + "].");
        }
    }

    /**
     * Checks that the username and password matches the ones of the publisher.
     *
     * @param username
     * @param password
     * @return
     */
    private boolean authenticatePublisher(String username, String password) {
        String pubUsername = configuration.chatMicroappUsername;
        String pubPassword = configuration.chatMicroappPassword;
        return !pubUsername.isEmpty() && !pubPassword.isEmpty() && username.equals(pubUsername) && password.equals(pubPassword);
    }
}
