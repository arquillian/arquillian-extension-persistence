package org.arquillian.ape.core;

import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.spi.PopulatorService;

public class MyPopulatorEnricher extends PopulatorEnricher<PopulatorService> {
    @Override
    public Populator createPopulator(PopulatorService populatorService) {
        return new MyPopulator(populatorService);
    }
}
