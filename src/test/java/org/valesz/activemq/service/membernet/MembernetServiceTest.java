package org.valesz.activemq.service.membernet;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MembernetServiceTest {

    private MembernetServiceConfiguration configuration = new MembernetServiceConfiguration("","");

    // unignore this test if you have correct username:access token combination and want to test it
    @Ignore
    @Test
    public void testAuthenticate() {
        final String username = "zdenek.vales@yoso.fi";
        final String accessToken = "correct token doesn't belong to repo :)";
        MembernetServiceImpl membernetService = new MembernetServiceImpl(configuration);

        boolean res = membernetService.authenticate(username, accessToken);
        assertTrue("Authentication failed!", res);
    }

    @Test
    public void testAuthenticate_fail() {
        final String username = "badusername";
        final String accessToken = "badtoken";
        MembernetServiceImpl membernetService = new MembernetServiceImpl(configuration);

        boolean res = membernetService.authenticate(username, accessToken);
        assertFalse("Authentication should have failed!", res);
    }

    // unignore this test if you have correct username:access token combination and want to test it
    @Ignore
    @Test
    public void testCanReadDestination() {
        final String destination = "queue://MN.discussion.1";
        final String accessToken = "correct token doesn't belong to repo :)";
        MembernetServiceImpl membernetService = new MembernetServiceImpl(configuration);

        boolean res = membernetService.canReadDestination(destination, accessToken);
        assertTrue("Should be able to read destination!", res);
    }

}
