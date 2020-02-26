package org.valesz.activemq.service.tronalddump;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * POC class not to be used in prod. Good enough for testing though.
 */
public class TronaldDumpService {

    private static final Logger LOG = LoggerFactory.getLogger(TronaldDumpService.class);

    /**
     * Returns random quote by the Omniscient himself.
     *
     * @return
     */
    public String getRandomQuote() {
        String apiUrl = getAuthApiUrl();
        if (apiUrl.isEmpty()) {
            return "";
        }

        Response r = ClientBuilder.newClient()
                .register(JacksonJsonProvider.class)
                .target(apiUrl)
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() != 200) {
            LOG.warn("Failed : HTTP error code : "
                    + r.getStatus());

            return "";
        }

        TronaldDumpQuote q = r.readEntity(TronaldDumpQuote.class);

        return q.getValue();
    }

    private String getAuthApiUrl() {
        Properties prop = new Properties();
        String propFileName = "plugin.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            try {
                prop.load(inputStream);

                return prop.getProperty("authentication.api.url","");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOG.warn("property file '" + propFileName + "' not found in the classpath");
        }

        return "";
    }
}
