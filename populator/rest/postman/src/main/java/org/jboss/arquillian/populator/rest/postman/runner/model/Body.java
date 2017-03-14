package org.jboss.arquillian.populator.rest.postman.runner.model;

import java.util.List;

public class Body {

    private Mode mode;
    private String raw;
    private List<UrlEncodedParameter> urlencoded;
    private List<FormParameter> formdata;

    public Body() {
    }

    public boolean isBodyWithMode() {
        return mode != null;
    }

    public Mode getMode() {
        return mode;
    }

    public List<FormParameter> getFormdata() {
        return formdata;
    }

    public List<UrlEncodedParameter> getUrlencoded() {
        return urlencoded;
    }

    public String getRaw() {
        // Postman json file might contain line terminators
        return raw.replaceAll("\\r|\\n", "");
    }
}
