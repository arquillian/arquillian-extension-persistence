package org.arquillian.ape.rest;

import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.core.PopulatorEnricher;

public class RestPopulatorEnricher extends PopulatorEnricher<RestPopulatorService> {
    @Override
    public Populator createPopulator(RestPopulatorService populatorService) {
        return new RestPopulator(populatorService);
    }
}
