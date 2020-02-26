package org.valesz.activemq.plugin.authorization;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.security.AuthorizationBroker;
import org.apache.activemq.security.AuthorizationMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This plugin provides queue-level authorization. Currently only intercepts access to MN.discussion.* queues.
 * This plugin is called only when client connects to the queue.
 */
public class ClientAuthorizationBroker extends AuthorizationBroker {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAuthorizationBroker.class);

    /**
     * Intercept access to these queues and use MN authorization.
     */
    private static final String MN_MESSAGE_Q_PREFIX = "queue://MN.discussion.";

    public ClientAuthorizationBroker(Broker next, AuthorizationMap authorizationMap) {
        super(next, authorizationMap);
    }

    @Override
    public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        if (isMembernetDiscussionMessage(info)) {
            return callMembernetAuthorization(context, info);
        } else {
            return super.addConsumer(context, info);
        }
    }

    /**
     * Returns true if the consumer is trying to access MN discussion queues.
     * @param info
     * @return
     */
    private boolean isMembernetDiscussionMessage(ConsumerInfo info) {
        return info.getDestination().getQualifiedName().startsWith(MN_MESSAGE_Q_PREFIX);
    }

    /**
     * Calls membernet's authorization service.
     *
     * @param context
     * @param info
     * @return
     */
    private Subscription callMembernetAuthorization(ConnectionContext context, ConsumerInfo info) throws Exception {
        LOG.info("Calling membernet authorization for user '{}' and destination '{}'.", context.getUserName(), info.getDestination());
        // todo: MN service
        return getNext().addConsumer(context, info);
    }


}
