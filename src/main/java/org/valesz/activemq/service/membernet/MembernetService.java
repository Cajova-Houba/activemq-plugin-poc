package org.valesz.activemq.service.membernet;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valesz.activemq.service.tronalddump.TronaldDumpService;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Service used to communicate with MN.
 */
public class MembernetService {

    private static final Logger LOG = LoggerFactory.getLogger(TronaldDumpService.class);

    /**
     * Checks whether the user with given access token is authorized to read
     * from destination.
     *
     * @param destination Qualified name of destination. Expected format is: MN.discussion.{discussionId}
     * @param accessToken OAuth2 access token.
     * @return True if user can read from the destination.
     */
    public boolean canReadDestination(String destination, String accessToken) {
        String apiUrl = getAuthApiUrl();
        if (apiUrl.isEmpty()) {
            return false;
        }

        try {
            apiUrl = apiUrl.replace("{discussionId}", destination.split(".")[2]);

            Response r = ClientBuilder.newClient()
                    .register(JacksonJsonProvider.class)
                    .target(apiUrl)
                    .request()
                    .header("Authorization", "Bearer "+accessToken)
                    .get();

            LOG.debug("Status code returned for 'canRead' call: {}.", r.getStatus());
            return r.getStatus() == 200;
        } catch (Exception ex) {
            LOG.error("Unexpected exception.", ex);
            return false;
        }
    }

    private String getAuthApiUrl() {
        Properties prop = new Properties();
        String propFileName = "plugin.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            try {
                prop.load(inputStream);

                return prop.getProperty("mn.canRead.api.url","");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOG.warn("property file '" + propFileName + "' not found in the classpath");
        }

        return "";
    }
}
