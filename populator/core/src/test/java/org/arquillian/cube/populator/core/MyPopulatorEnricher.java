package org.arquillian.cube.populator.core;

import org.arquillian.cube.populator.spi.PopulatorService;

public class MyPopulatorEnricher extends PopulatorEnricher<PopulatorService> {
    @Override
    public Populator createPopulator(PopulatorService populatorService) {
        return new MyPopulator(populatorService);
    }
}
