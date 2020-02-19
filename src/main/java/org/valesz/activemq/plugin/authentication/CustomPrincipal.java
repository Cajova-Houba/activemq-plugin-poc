package org.valesz.activemq.plugin.authentication;

import java.security.Principal;

public class CustomPrincipal implements Principal {

    private final String value;

    public CustomPrincipal(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return "custom principal";
    }

    public String getValue() {
        return value;
    }
}
