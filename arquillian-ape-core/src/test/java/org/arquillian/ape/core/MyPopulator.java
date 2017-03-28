package org.arquillian.ape.core;

import org.arquillian.ape.spi.PopulatorService;

public class MyPopulator extends Populator<PopulatorService, MyPopulatorConfigurator> {
    public MyPopulator(PopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public MyPopulatorConfigurator createExecutor() {
        return new MyPopulatorConfigurator(this.populatorService);
    }
}
