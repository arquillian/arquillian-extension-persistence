package org.arquillian.ape.rest.postman.runner.model;

import java.util.List;

public class Folder implements Item {

    private String name;
    private String description;
    private List<Item> item;

    public String getName() {
        return name;
    }

    public List<Item> getItem() {
        return item;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.FOLDER;
    }
}
