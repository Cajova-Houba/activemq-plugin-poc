package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.valesz.activemq.service.membernet.MembernetService;

public class CustomAuthenticationPlugin implements BrokerPlugin {

    private MembernetService membernetService;

    private CustomAuthenticationBrokerConfiguration configuration;

    public CustomAuthenticationPlugin(MembernetService membernetService, CustomAuthenticationBrokerConfiguration configuration) {
        this.membernetService = membernetService;
        this.configuration = configuration;
    }

    @Override
    public Broker installPlugin(Broker next) throws Exception {
        return new CustomAuthenticationBroker(next, configuration, membernetService);
    }
}
