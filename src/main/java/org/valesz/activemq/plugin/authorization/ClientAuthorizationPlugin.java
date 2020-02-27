package org.valesz.activemq.plugin.authorization;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.security.AuthorizationMap;
import org.apache.activemq.security.AuthorizationPlugin;
import org.valesz.activemq.service.membernet.MembernetService;

/**
 * @org.apache.xbean.XBean element="clientAuthorizationPlugin"
 *
 */
public class ClientAuthorizationPlugin extends AuthorizationPlugin {

    private MembernetService membernetService;

    public ClientAuthorizationPlugin(AuthorizationMap map, MembernetService membernetService) {
        super(map);
        this.membernetService = membernetService;
    }

    @Override
    public Broker installPlugin(Broker broker) {
        if (getMap() == null) {
            throw new IllegalArgumentException("You must configure a 'map' property");
        }
        return new ClientAuthorizationBroker(broker, getMap(), membernetService);
    }
}
