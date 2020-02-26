package org.valesz.activemq.service.tronalddump;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class TronaldDumpServiceTest {

    @Test
    public void testGetRandomQuote() {
        TronaldDumpService service = new TronaldDumpService();

        String q = service.getRandomQuote();
        assertFalse("Empty quote returned!", q.isEmpty());

    }
}
