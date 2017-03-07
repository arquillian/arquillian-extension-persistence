package org.arquillian.cube.populator.core;

import org.arquillian.cube.populator.spi.PopulatorService;

public class MyPopulator extends Populator<PopulatorService, MyPopulatorConfigurator> {
    public MyPopulator(PopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public MyPopulatorConfigurator createExecutor() {
        return new MyPopulatorConfigurator(this.populatorService);
    }
}
