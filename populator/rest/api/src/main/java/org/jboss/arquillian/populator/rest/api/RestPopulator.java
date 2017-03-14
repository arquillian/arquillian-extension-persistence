package org.jboss.arquillian.populator.rest.api;


import org.jboss.arquillian.populator.core.Populator;

public class RestPopulator extends Populator<RestPopulatorService, RestPopulatorConfigurator> {

    public RestPopulator(RestPopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public RestPopulatorConfigurator createExecutor() {
        return new RestPopulatorConfigurator(this.host, this.bindPort, this.populatorService);
    }
}
