package org.jboss.arquillian.populator.core;

import org.jboss.arquillian.populator.spi.PopulatorService;

/**
 * This class represents base class for all Populators DSL for storing configuration.
 * It implements common operations o to configure all populators.
 *
 * @param <T> Type of service that implements operations with configured parameters.
 * @param <R> Type of custom DSLs for specific populators.
 */
public abstract class Populator<T extends PopulatorService, R extends Populator.PopulatorConfigurator> {

    protected T populatorService;
    protected String host;
    protected int bindPort;

    protected Populator(T populatorService) {
        this.populatorService = populatorService;
    }

    /**
     * Method that needs to be implemented that implements custom DSL methods.
     * For example in case of SQL databases some parameters like driver class or JDBC are required meanwhile in case of NoSQL you only need database name.
     * @return Class implementing {@link PopulatorConfigurator}
     */
    public abstract R createExecutor();

    /**
     * Initial method for Populators DSL
     * @param hostname of service to populate.
     * @param port exposed by the service
     * @return Next commands to configure service.
     */
    public R forServer(String hostname, int port) {
        this.host = hostname;
        this.bindPort = port;
        return createExecutor();
    }

    /**
     * Populator Configuration interface that all custom DSLs must implements.
     * It defines the DSL terminators.
     */
    public interface PopulatorConfigurator {

        /**
         * Terminator method that executes the datasets against configured service.
         */
        void execute();

        /**
         * Terminator method that clean configures service.
         */
        void clean();

    }
}
