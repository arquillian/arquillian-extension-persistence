package org.jboss.arquillian.populator.rest.api;

import org.jboss.arquillian.populator.core.Populator;
import org.jboss.arquillian.populator.core.PopulatorEnricher;

public class RestPopulatorEnricher extends PopulatorEnricher<RestPopulatorService> {
    @Override
    public Populator createPopulator(RestPopulatorService populatorService) {
        return new RestPopulator(populatorService);
    }
}
