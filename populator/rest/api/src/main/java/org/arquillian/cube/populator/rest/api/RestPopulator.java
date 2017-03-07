package org.arquillian.cube.populator.rest.api;


import org.arquillian.cube.populator.core.Populator;

public class RestPopulator extends Populator<RestPopulatorService, RestPopulatorConfigurator> {

    public RestPopulator(RestPopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public RestPopulatorConfigurator createExecutor() {
        return new RestPopulatorConfigurator(this.host, this.bindPort, this.populatorService);
    }
}
