package org.jboss.arquillian.populator.core;

import org.jboss.arquillian.populator.spi.PopulatorService;

public class MyPopulator extends Populator<PopulatorService, MyPopulatorConfigurator> {
    public MyPopulator(PopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public MyPopulatorConfigurator createExecutor() {
        return new MyPopulatorConfigurator(this.populatorService);
    }
}
