package org.arquillian.ape.rest.postman.runner.model;

public class UrlEncodedParameter {

    private String key;
    private String value;
    private boolean enabled;

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
