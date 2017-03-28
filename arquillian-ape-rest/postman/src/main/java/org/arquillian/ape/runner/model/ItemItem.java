package org.jboss.arquillian.populator.rest.postman.runner.model;

import java.net.URL;

public class ItemItem implements Item {

    private String id;
    private String name;

    // Request can be a URL directly or a complex request object.
    private URL request;
    private Request requestObject;

    // Response is ignored in this case.


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequest(URL request) {
        this.request = request;
    }

    public void setRequestObject(Request requestObject) {
        this.requestObject = requestObject;
    }

    public boolean isRequestAsString() {
        return this.request != null;
    }

    public Request getRequestObject() {
        return requestObject;
    }

    public URL getRequest() {
        return request;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.ITEM;
    }
}
