package org.jboss.arquillian.populator.nosql.api;

import org.jboss.arquillian.populator.core.Populator;
import org.jboss.arquillian.populator.core.PopulatorEnricher;

/**
 * Implementation of Populator enricher for Sql support.
 */
public class SqlPopulatorEnricher extends PopulatorEnricher<SqlPopulatorService> {
    @Override
    public Populator createPopulator(SqlPopulatorService populatorService) {
        return new SqlPopulator(populatorService);
    }
}
