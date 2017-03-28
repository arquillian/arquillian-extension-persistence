package org.arquillian.ape.rest.postman.runner.model;

/**
 * Items can be an item or folder.
 */
public interface Item {

    /**
     * Method to avoid cost of instanceof
     * @return Type of element.
     */
    ItemType getItemType();

}
