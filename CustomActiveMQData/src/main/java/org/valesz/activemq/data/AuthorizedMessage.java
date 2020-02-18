package org.valesz.activemq.data;

import java.io.Serializable;
import java.util.List;

/**
 * Message (actual payload) sent from publisher to broker and from MQ to listeners.
 */
public class AuthorizedMessage implements Serializable {

    private final int id;

    private final List<String> authorizedClients;

    public AuthorizedMessage(int id, List<String> authorizedClients) {
        this.id = id;
        this.authorizedClients = authorizedClients;
    }

    public int getId() {
        return id;
    }

    public List<String> getAuthorizedClients() {
        return authorizedClients;
    }

    @Override
    public String toString() {
        return "AuthorizedMessage{" +
                "id=" + id +
                ", authorizedClients=" + authorizedClients +
                '}';
    }
}
