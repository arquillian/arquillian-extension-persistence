package org.jboss.arquillian.populator.rest.postman.runner.model;

public class Version {

    private String major;
    private String minor;
    private String patch;
    private String identifier;

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getPatch() {
        return patch;
    }

    public String getIdentifier() {
        return identifier;
    }
}
