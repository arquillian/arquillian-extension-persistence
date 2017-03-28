package org.jboss.arquillian.populator.rest.postman.runner.model;

public class FormParameter {

    private String key;
    private String value;
    private boolean enabled;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
