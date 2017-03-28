package org.arquillian.ape.rest.postman.runner.model;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private Url url;

    private Method method;
    private Map<String, String> headers = new HashMap<>();
    private Body body;

    public void setUrl(Url url) {
        this.url = url;
    }

    public Url getUrl() {
        return url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }
}
