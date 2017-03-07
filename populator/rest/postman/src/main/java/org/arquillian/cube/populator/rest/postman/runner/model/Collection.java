package org.arquillian.cube.populator.rest.postman.runner.model;

import java.util.List;

public class Collection {

    private Information info;
    private List<Item> item;

    // Variables are pre-read it to have them during parsing.


    public void setInfo(Information info) {
        this.info = info;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }


    public Information getInfo() {
        return info;
    }

    public List<Item> getItem() {
        return item;
    }

}
