package org.valesz.activemq.data;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class AuthorizedMessageTest {

    @Test
    public void testByteSerialization() {

        AuthorizedMessage msg = new AuthorizedMessage(1, "text", Collections.singletonList("client"));
        byte[] serializedMsg = msg.toBytes();
        System.out.println("Serialized msg: "+new String(serializedMsg));
        AuthorizedMessage deSerializedMsg = AuthorizedMessage.fromBytes(serializedMsg);

        assertEquals("Messages are not equal!", msg, deSerializedMsg);
    }
}
