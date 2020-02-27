package org.valesz.activemq.service.membernet;

public interface MembernetService {

    /**
     * Checks whether the user with given access token is authorized to read
     * from destination.
     *
     * @param destination Qualified name of destination. Expected format is: MN.discussion.{discussionId}
     * @param accessToken OAuth2 access token.
     * @return True if user can read from the destination.
     */
    boolean canReadDestination(String destination, String accessToken);

    /**
     * Authenticates the username and access token.
     *
     * Calls users/me endpoint (that is protected by OAuth) of MN and
     * if returned object has same username, returns true.
     *
     * @param username Membernet username.
     * @param accessToken OAuth2 token that is used to access MN API.
     * @return
     */
    boolean authenticate(String username, String accessToken);
}
