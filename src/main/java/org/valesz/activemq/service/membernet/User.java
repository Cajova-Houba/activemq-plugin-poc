package org.valesz.activemq.service.membernet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User object retrieved from MN.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private Long id;

    private String username;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
