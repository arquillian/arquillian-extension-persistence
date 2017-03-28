package org.jboss.arquillian.populator.rdbms.api;

import org.jboss.arquillian.populator.core.Populator;

public class RdbmsPopulator extends Populator<RdbmsPopulatorService, RdbmsPopulatorConfigurator> {

    public RdbmsPopulator(RdbmsPopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public RdbmsPopulatorConfigurator createExecutor() {
        if (this.uri != null) {
            return new RdbmsPopulatorConfigurator(this.uri, this.populatorService);
        } else {
            throw new IllegalArgumentException("For SQL case use forUri method to set JDBC Url");
        }
    }

}
