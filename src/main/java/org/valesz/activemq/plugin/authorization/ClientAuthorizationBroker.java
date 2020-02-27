package org.valesz.activemq.plugin.authorization;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.security.AuthorizationBroker;
import org.apache.activemq.security.AuthorizationMap;
import org.apache.activemq.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valesz.activemq.plugin.authentication.CustomPrincipal;
import org.valesz.activemq.service.membernet.MembernetService;
import org.valesz.activemq.service.membernet.MembernetServiceImpl;

import java.security.Principal;

/**
 * This plugin provides queue-level authorization. Currently only intercepts access to MN.discussion.* queues.
 * This plugin is called only when client connects to the queue.
 */
public class ClientAuthorizationBroker extends AuthorizationBroker {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAuthorizationBroker.class);

    private MembernetService membernetService;

    /**
     * Intercept access to these queues and use MN authorization.
     */
    private static final String MN_MESSAGE_Q_PREFIX = "queue://MN.discussion.";

    public ClientAuthorizationBroker(Broker next, AuthorizationMap authorizationMap, MembernetService membernetService) {
        super(next, authorizationMap);
        this.membernetService = membernetService;
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
        SecurityContext sc = context.getSecurityContext();
        if (sc == null) {
            LOG.error("No security context, client should be authenticated at this point!");
            throw new SecurityException("No security context, client should be authenticated at this point!");
        }

        // find principal with access token
        CustomPrincipal accessTokenPrincipal = null;
        for(Principal p : sc.getPrincipals()) {
            if (p instanceof CustomPrincipal) {
                accessTokenPrincipal = (CustomPrincipal) p;
            }
        }

        if (accessTokenPrincipal == null) {
            LOG.error("No principal with access token for user '{}'.", context.getUserName());
            throw new SecurityException("No access token found among user's principals.");
        }

        MembernetServiceImpl membernetService = new MembernetServiceImpl();
        String destination = info.getDestination().getQualifiedName();

        if (!membernetService.canReadDestination(destination, accessTokenPrincipal.getValue())) {
            LOG.error("User '{}' is not authorized to read destination '{}'.", context.getUserName(), destination);
            throw new SecurityException("User '"+context.getUserName()+"' is not authorized to read destination '"+destination+"'.");
        }

        // authorization ok, continue in broker filter chain
        return getNext().addConsumer(context, info);
    }


}
