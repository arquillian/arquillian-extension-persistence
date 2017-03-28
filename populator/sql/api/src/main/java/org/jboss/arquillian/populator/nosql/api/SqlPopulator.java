package org.jboss.arquillian.populator.nosql.api;

import org.jboss.arquillian.populator.core.Populator;

/**
 * NoSql Populator that creates the NoSqlPopulatorConfigurator with specific DSL methods related to NoSql engines.
 * @see SqlPopulatorConfigurator
 */
public class SqlPopulator extends Populator<SqlPopulatorService, SqlPopulatorConfigurator> {

    public SqlPopulator(SqlPopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public SqlPopulatorConfigurator createExecutor() {
        if (this.uri != null) {
            return new SqlPopulatorConfigurator(this.uri, this.populatorService);
        } else {
            throw new IllegalArgumentException("For SQL case use forUri method to set JDBC Url");
        }
    }

}
