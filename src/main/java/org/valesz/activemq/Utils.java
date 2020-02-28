package org.valesz.activemq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    /**
     * Initializes new properties object from given property file.
     *
     * @param propertyFileName
     * @return Property file or null on failure.
     */
    public static Properties initializeProperties(String propertyFileName) {
        try {
            InputStream inputStream = Utils.class.getClassLoader().getResourceAsStream(propertyFileName);
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
}
