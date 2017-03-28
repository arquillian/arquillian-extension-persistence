package org.jboss.arquillian.populator.rdbms.api;

import org.jboss.arquillian.populator.core.Populator;
import org.jboss.arquillian.populator.core.PopulatorEnricher;

/**
 * Implementation of Populator enricher for Sql support.
 */
public class RdbmsPopulatorEnricher extends PopulatorEnricher<RdbmsPopulatorService> {
    @Override
    public Populator createPopulator(RdbmsPopulatorService populatorService) {
        return new RdbmsPopulator(populatorService);
    }
}
