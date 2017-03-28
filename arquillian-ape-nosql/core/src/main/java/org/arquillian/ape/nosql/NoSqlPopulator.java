package org.arquillian.ape.nosql;

import org.arquillian.ape.core.Populator;

/**
 * NoSql Populator that creates the NoSqlPopulatorConfigurator with specific DSL methods related to NoSql engines.
 * @see NoSqlPopulatorConfigurator
 */
public class NoSqlPopulator extends Populator<NoSqlPopulatorService, NoSqlPopulatorConfigurator> {

    public NoSqlPopulator(NoSqlPopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public NoSqlPopulatorConfigurator createExecutor() {

        if (this.uri != null) {
            return new NoSqlPopulatorConfigurator(this.uri, this.populatorService);
        } else {
            return new NoSqlPopulatorConfigurator(this.host, this.bindPort, this.populatorService);
        }
    }

}
