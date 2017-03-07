package org.arquillian.cube.populator.nosql.api;

import org.arquillian.cube.populator.core.Populator;
import org.arquillian.cube.populator.core.PopulatorEnricher;

/**
 * Implementation of Populator enricher for NoSql support.
 */
public class NoSqlPopulatorEnricher extends PopulatorEnricher<NoSqlPopulatorService> {
    @Override
    public Populator createPopulator(NoSqlPopulatorService populatorService) {
        return new NoSqlPopulator(populatorService);
    }
}
