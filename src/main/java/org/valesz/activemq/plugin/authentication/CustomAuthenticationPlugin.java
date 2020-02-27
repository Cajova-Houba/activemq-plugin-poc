package org.valesz.activemq.plugin.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.valesz.activemq.service.membernet.MembernetService;
import org.valesz.activemq.service.membernet.MembernetServiceImpl;

public class CustomAuthenticationPlugin implements BrokerPlugin {

    private MembernetService membernetService;

    public CustomAuthenticationPlugin(MembernetService membernetService) {
        this.membernetService = membernetService;
    }

    @Override
    public Broker installPlugin(Broker next) throws Exception {
        return new CustomAuthenticationBroker(next, membernetService);
    }
}
