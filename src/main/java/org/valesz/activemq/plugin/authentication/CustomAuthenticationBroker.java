package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        System.out.println("Calling authentication API");

        String authRes = callAuthenticationApiNoDependencies(username, password);

        return new SecurityContext(username) {
            @Override
            public Set<Principal> getPrincipals() {
                Principal p = new CustomPrincipal(authRes);
                return Collections.singleton(p);
            }
        };
    }

    private String callAuthenticationApiNoDependencies(String username, String password) {
        String result = "";
        try {

            URL url = new URL("https://www.tronalddump.io/random/quote");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                System.out.println("Failed : HTTP error code : "
                        + conn.getResponseCode());

                return result;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder outputSb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                outputSb.append(output);
            }

            conn.disconnect();

            Matcher m = Pattern.compile("\"value\":\"([\\w\\d\\s\"&'.!,?-]+)\",").matcher(outputSb.toString());
            if (m.find()) {
                result = m.group(1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
