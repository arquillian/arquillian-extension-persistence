package org.arquillian.cube.populator.core;

import org.arquillian.cube.populator.spi.PopulatorService;

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
