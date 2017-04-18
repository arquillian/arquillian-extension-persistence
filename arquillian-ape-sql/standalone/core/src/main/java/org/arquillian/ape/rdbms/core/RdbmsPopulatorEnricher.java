package org.arquillian.ape.rdbms.core;

import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.core.PopulatorEnricher;

/**
 * Implementation of Populator enricher for Sql support.
 */
public class RdbmsPopulatorEnricher extends PopulatorEnricher<RdbmsPopulatorService> {
    @Override
    public Populator createPopulator(RdbmsPopulatorService populatorService) {
        return new RdbmsPopulator(populatorService);
    }
}
