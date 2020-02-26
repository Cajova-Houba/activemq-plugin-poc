package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valesz.activemq.service.tronalddump.TronaldDumpService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomAuthenticationBroker extends AbstractAuthenticationBroker {

    private static final Logger LOG = LoggerFactory.getLogger(CustomAuthenticationBroker.class);

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
        LOG.info("Authenticating user '{}'", username);

        LOG.trace("Calling authentication API");

        String authRes = callAuthenticationApiNoDependencies(username, password);

        return new SecurityContext(username) {
            @Override
            public Set<Principal> getPrincipals() {
                Principal p = new CustomPrincipal(authRes);

                Principal group = new GroupPrincipal("admin".equals(username) ? "admins" : "users");

                return new HashSet<>(Arrays.asList(p, group));
            }
        };
    }

    private String callAuthenticationApiNoDependencies(String username, String password) {
        try {

            return callApi(username, password);

        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Unexpected exception: ",e);
            return "";
        }
    }

    private String callApi(String username, String password) throws IOException {

        TronaldDumpService service = new TronaldDumpService();
        return service.getRandomQuote();
    }
}
