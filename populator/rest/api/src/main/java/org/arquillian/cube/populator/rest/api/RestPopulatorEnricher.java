package org.arquillian.cube.populator.rest.api;

import org.arquillian.cube.populator.core.Populator;
import org.arquillian.cube.populator.core.PopulatorEnricher;

public class RestPopulatorEnricher extends PopulatorEnricher<RestPopulatorService> {
    @Override
    public Populator createPopulator(RestPopulatorService populatorService) {
        return new RestPopulator(populatorService);
    }
}
