package org.arquillian.ape.nosql;


import org.arquillian.ape.core.Populator;
import org.arquillian.ape.core.PopulatorEnricher;

/**
 * Implementation of Populator enricher for NoSql support.
 */
public class NoSqlPopulatorEnricher extends PopulatorEnricher<NoSqlPopulatorService> {
    @Override
    public Populator createPopulator(NoSqlPopulatorService populatorService) {
        return new NoSqlPopulator(populatorService);
    }
}
