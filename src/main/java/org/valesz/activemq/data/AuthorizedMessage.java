package org.valesz.activemq.data;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Message (actual payload) sent from publisher to broker and from MQ to listeners.
 *
 * TextMessage seems to be the most convenient way of transporting messages while begin able
 * to access some metadata in the authorization plugin.
 */
public class AuthorizedMessage implements Serializable {

    public static AuthorizedMessage fromBytes(byte[] bytes) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            AuthorizedMessage msg = (AuthorizedMessage) in.readObject();

            in.close();
            return msg;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static AuthorizedMessage fromTextMessage(TextMessage textMessage) throws JMSException {
        String text = textMessage.getText();
        String authorizedClients = textMessage.getStringProperty("authorizedClients");
        int id = textMessage.getIntProperty("id");

        return new AuthorizedMessage(id, text, Arrays.asList(authorizedClients.split(";")));
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

    public String getClientsAsString() {
        StringBuilder sb = new StringBuilder();

        for(String client : getAuthorizedClients()) {
            sb.append(client).append(';');
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }

        return sb.toString();
    }

    public TextMessage toTextMessage(Session session) throws JMSException {
        TextMessage tm = session.createTextMessage(getText());
        tm.setIntProperty("id", id);
        tm.setStringProperty("messageType", getClass().getSimpleName());
        tm.setStringProperty("authorizedClients", getClientsAsString());

        return tm;
    }

    @Override
    public String toString() {
        return "AuthorizedMessage{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", authorizedClients=" + authorizedClients +
                '}';
    }

    public byte[] toBytes() {
        byte[] res = new byte[0];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            res = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizedMessage that = (AuthorizedMessage) o;
        return id == that.id &&
                Objects.equals(text, that.text) &&
                Objects.equals(authorizedClients, that.authorizedClients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, authorizedClients);
    }
}
