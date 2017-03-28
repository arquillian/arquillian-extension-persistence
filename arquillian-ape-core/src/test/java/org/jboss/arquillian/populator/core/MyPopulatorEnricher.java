package org.jboss.arquillian.populator.core;

import org.jboss.arquillian.populator.spi.PopulatorService;

public class MyPopulatorEnricher extends PopulatorEnricher<PopulatorService> {
    @Override
    public Populator createPopulator(PopulatorService populatorService) {
        return new MyPopulator(populatorService);
    }
}
