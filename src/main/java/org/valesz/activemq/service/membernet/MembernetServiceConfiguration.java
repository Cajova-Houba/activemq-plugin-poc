package org.valesz.activemq.service.membernet;

/**
 * Simple class used to pass configuration from xml into the plugin.
 */
public class MembernetServiceConfiguration {

    public final String userDetailsUrl;

    public final String canReadDiscussionUrl;

    public MembernetServiceConfiguration(String userDetailsUrl, String canReadDiscussionUrl) {
        this.userDetailsUrl = userDetailsUrl;
        this.canReadDiscussionUrl = canReadDiscussionUrl;
    }
}
