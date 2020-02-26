package org.valesz.activemq.plugin.authorization;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.security.AuthorizationMap;
import org.apache.activemq.security.AuthorizationPlugin;

/**
 * @org.apache.xbean.XBean element="clientAuthorizationPlugin"
 *
 */
public class ClientAuthorizationPlugin extends AuthorizationPlugin {

    public ClientAuthorizationPlugin() {
    }

    public ClientAuthorizationPlugin(AuthorizationMap map) {
        super(map);
    }

    @Override
    public Broker installPlugin(Broker broker) {
        if (getMap() == null) {
            throw new IllegalArgumentException("You must configure a 'map' property");
        }
        return new ClientAuthorizationBroker(broker, getMap());
    }
}
