package org.valesz.activemq.plugin.authentication;

/**
 * Simple class used to pass configuration from xml to authentication broker.
 */
public class CustomAuthenticationBrokerConfiguration {

    public final String chatMicroappUsername;

    public final String chatMicroappPassword;

    public CustomAuthenticationBrokerConfiguration(String chatMicroappUsername, String chatMicroappPassword) {
        this.chatMicroappUsername = chatMicroappUsername;
        this.chatMicroappPassword = chatMicroappPassword;
    }
}
