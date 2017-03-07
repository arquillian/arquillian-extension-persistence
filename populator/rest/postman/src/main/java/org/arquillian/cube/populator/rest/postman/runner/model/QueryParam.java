package org.arquillian.cube.populator.rest.postman.runner.model;

import java.net.URLEncoder;

public class QueryParam {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String asString() {
        return URLEncoder.encode(this.getKey()) + "=" + URLEncoder.encode(this.getValue());
    }

}
