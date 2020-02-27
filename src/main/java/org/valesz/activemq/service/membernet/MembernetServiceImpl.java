package org.valesz.activemq.service.membernet;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Properties;

/**
 * Service used to communicate with MN.
 */
public class MembernetServiceImpl implements MembernetService {

    private static final Logger LOG = LoggerFactory.getLogger(MembernetServiceImpl.class);

    private static final String PROPERTY_FILE_NAME = "plugin.properties";
    private static final String IS_AUTH_TO_READ_DISSCUSION_PROP = "mn.canRead.api.url";
    private static final String GET_USER_DETAILS_PROP = "mn.user.details.api.url";

    private Properties properties;

    public MembernetServiceImpl() {
        LOG.info("Creating new instance of MN service. Property file name: {}.", PROPERTY_FILE_NAME);
        this.properties = initializeProperties(PROPERTY_FILE_NAME);
    }

    /**
     * Checks whether the user with given access token is authorized to read
     * from destination.
     *
     * @param destination Qualified name of destination. Expected format is: MN.discussion.{discussionId}
     * @param accessToken OAuth2 access token.
     * @return True if user can read from the destination.
     */
    public boolean canReadDestination(String destination, String accessToken) {
        String apiUrl = getProperty(IS_AUTH_TO_READ_DISSCUSION_PROP);
        if (apiUrl.isEmpty()) {
            return false;
        }

        try {
            String[] splitDestination = destination.split("[.]");
            if (splitDestination.length != 3) {
                LOG.warn("Unexpected destination format: {}", destination);
                return false;
            }
            apiUrl = apiUrl.replace("{discussionId}", splitDestination[2]);

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

    /**
     * Authenticates the username and access token.
     *
     * Calls users/me endpoint (that is protected by OAuth) of MN and
     * if returned object has same username, returns true.
     *
     * @param username Membernet username.
     * @param accessToken OAuth2 token that is used to access MN API.
     * @return
     */
    public boolean authenticate(String username, String accessToken) {
        LOG.info("Calling MN API to authenticate user '{}'.", username);

        if (username == null || username.isEmpty() || accessToken == null || accessToken.isEmpty()) {
            LOG.warn("No username or no access token.");
            return false;
        }

        String apiUrl = getProperty(GET_USER_DETAILS_PROP);
        if (apiUrl.isEmpty()) {
            LOG.warn("No API url.");
            return false;
        }

        try {
            Response r = ClientBuilder.newClient()
                    .register(JacksonJsonProvider.class)
                    .target(apiUrl)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Bearer "+accessToken)
                    .get();

            if (r.getStatus() != 200) {
                LOG.warn("Status code returned for 'canRead' call: {}.", r.getStatus());
                return false;
            }

            User u = r.readEntity(User.class);
            if (!username.equals(u.getUsername())) {
                LOG.warn("Authentication failed for user '{}'.", username);
                return false;
            }

            return true;
        } catch (Exception ex) {
            LOG.error("Unexpected exception.", ex);
            return false;
        }
    }

    /**
     * Initializes new properties object from given property file.
     *
     * @param propertyFileName
     * @return Property file or null on failure.
     */
    private Properties initializeProperties(String propertyFileName) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName);
            if (inputStream != null) {
                Properties p = new Properties();
                p.load(inputStream);
                return p;
            } else {
                LOG.warn("property file '{}' not found in the classpath.", propertyFileName);
            }
        } catch (Exception ex) {
            LOG.error("Unexpected exception while loading properties from file '{}'.", propertyFileName);
            LOG.error("Details: ", ex);
        }

        return null;
    }

    private String getProperty(String key) {
        if (properties == null) {
            return "";
        }

        return properties.getProperty(key, "");
    }
}
