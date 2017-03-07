package org.arquillian.cube.populator.rest.postman.runner.model;

import java.net.MalformedURLException;
import java.net.URL;

public class Url {

    private URL url;
    private CompleteUrl urlComplete;

    public Url(URL url) {
        this.url = url;
    }

    public Url(CompleteUrl url) {
        this.urlComplete = url;
    }

    public URL asNativeUrl() {
        if (this.url != null) {
            return url;
        } else {
            try {
                return this.urlComplete.getAsUrl();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
