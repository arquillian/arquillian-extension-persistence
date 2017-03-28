package org.jboss.arquillian.populator.rest.api;


import org.jboss.arquillian.populator.core.Populator;

public class RestPopulator extends Populator<RestPopulatorService, RestPopulatorConfigurator> {

    public RestPopulator(RestPopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public RestPopulatorConfigurator createExecutor() {
        if (this.uri != null) {
            return new RestPopulatorConfigurator(uri, populatorService);
        } else {
            return new RestPopulatorConfigurator(this.host, this.bindPort, this.populatorService);
        }
    }
}
