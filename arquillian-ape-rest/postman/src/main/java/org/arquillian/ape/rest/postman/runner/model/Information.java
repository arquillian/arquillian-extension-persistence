package org.arquillian.ape.rest.postman.runner.model;

import java.net.URL;

public class Information {

    private String name;
    private URL schema;
    private Version version;

    public Information(String name, URL schema, Version version) {
        this.name = name;
        this.schema = schema;
        this.version = version;
    }

    public String getName() {
        return name;
    }
    public URL getSchema() {
        return schema;
    }
}
