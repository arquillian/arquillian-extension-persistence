package org.arquillian.ape.core;

import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.spi.PopulatorService;

public class MyPopulatorConfigurator implements Populator.PopulatorConfigurator {

    private PopulatorService populatorService;
    private String database;

    public MyPopulatorConfigurator(PopulatorService populatorService) {
        this.populatorService = populatorService;
    }

    public MyPopulatorConfigurator withDatabase(String database) {
        this.database = database;
        return this;
    }

    @Override
    public void execute() {

    }

    @Override
    public void clean() {

    }
}
