package org.valesz.activemq.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Message (actual payload) sent from publisher to broker and from MQ to listeners.
 */
public class AuthorizedMessage implements Serializable {

    public static AuthorizedMessage fromByteSequence(byte[] bytes) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            AuthorizedMessage msg = (AuthorizedMessage) in.readObject();

            in.close();
            return msg;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private final int id;

    private final String text;

    private final List<String> authorizedClients;

    public AuthorizedMessage(int id, String text, List<String> authorizedClients) {
        this.id = id;
        this.text = text;
        this.authorizedClients = authorizedClients;
    }

    public int getId() {
        return id;
    }

    public List<String> getAuthorizedClients() {
        return authorizedClients;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "AuthorizedMessage{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", authorizedClients=" + authorizedClients +
                '}';
    }
}
