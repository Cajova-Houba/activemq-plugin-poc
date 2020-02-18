package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class CustomAuthenticationPlugin implements BrokerPlugin {

    @Override
    public Broker installPlugin(Broker next) throws Exception {
        return new CustomAuthenticationBroker(next);
    }
}
