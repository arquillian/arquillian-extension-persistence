package org.jboss.arquillian.populator.nosql.api;

import org.jboss.arquillian.populator.core.Populator;
import org.jboss.arquillian.populator.core.PopulatorEnricher;

/**
 * Implementation of Populator enricher for NoSql support.
 */
public class NoSqlPopulatorEnricher extends PopulatorEnricher<NoSqlPopulatorService> {
    @Override
    public Populator createPopulator(NoSqlPopulatorService populatorService) {
        return new NoSqlPopulator(populatorService);
    }
}
