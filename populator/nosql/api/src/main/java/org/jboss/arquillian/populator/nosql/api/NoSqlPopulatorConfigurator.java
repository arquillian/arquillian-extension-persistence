package org.jboss.arquillian.populator.nosql.api;

import org.jboss.arquillian.populator.core.Populator;

import java.util.*;

/**
 * Implementation of NoSql DSL configurator.
 */
public class NoSqlPopulatorConfigurator implements Populator.PopulatorConfigurator {

    private String host;
    private int bindPort;
    private NoSqlPopulatorService populatorService;
    private String database;
    private List<String> datasets = new ArrayList<>();
    private Map<String, Object> options = new HashMap<>();

    NoSqlPopulatorConfigurator(String host, int bindPort, NoSqlPopulatorService populatorService) {
        this.host = host;
        this.bindPort = bindPort;
        this.populatorService = populatorService;
    }

    /**
     * Sets database name.
     * @param database name.
     * @return this instance.
     */
    public NoSqlPopulatorConfigurator withStorage(String database) {
        this.database = database;
        return this;
    }

    /**
     * Register a new dataset for populating data.
     * @param dataset to use.
     * @return this instance.
     */
    public NoSqlPopulatorConfigurator usingDataSet(String dataset) {
        this.datasets.add(dataset);
        return this;
    }

    /**
     * Register new datasets for populating data.
     * @param datasets to use.
     * @return this instance.
     */
    public NoSqlPopulatorConfigurator usingDataSets(String... datasets) {
        this.datasets.addAll(Arrays.asList(datasets));
        return this;
    }

    /**
     * Set custom options.
     * @param key name.
     * @param value of property.
     * @param elements pair key, value. Even elements are keys, odd ones values.
     * @return this instance.
     */
    public NoSqlPopulatorConfigurator withOption(String key, String value, String... elements) {

        if (elements.length % 2 != 0) {
            throw new IllegalArgumentException("Extra options should be passed in form of (key, value)");
        }

        this.options.put(key, value);

        for (int i=0; i < elements.length; i+=2) {
            this.options.put(elements[i], elements[i+1]);
        }

        return this;
    }

    /**
     * Set custom options as map.
     * @param options to set.
     * @return this instance.
     */
    public NoSqlPopulatorConfigurator withOptions(Map<String, Object> options) {
        this.options.putAll(options);
        return this;
    }

    @Override
    public void execute() {
        // TODO Improve this so connect and disconnect only happens once.
        // This implies for example observing @AfterClass to disconnect and add some boolean to know that connection is already started in execute and clean method.
        try {
            populatorService.connect(host, bindPort, this.database, this.options);
            populatorService.execute(Collections.unmodifiableList(datasets));
        } finally {
            populatorService.disconnect();
        }
    }

    public void clean() {
        try {
            populatorService.connect(host, bindPort, this.database, this.options);
            populatorService.clean();
        } catch (UnsupportedOperationException e) {
            //Nothing to do just log
        } finally {
            populatorService.disconnect();
        }
    }
}
