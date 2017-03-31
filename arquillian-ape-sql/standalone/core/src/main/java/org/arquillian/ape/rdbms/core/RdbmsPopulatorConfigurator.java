package org.arquillian.ape.rdbms.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.arquillian.ape.core.Populator;

public class RdbmsPopulatorConfigurator implements Populator.PopulatorConfigurator {

    private RdbmsPopulatorService populatorService;
    private URI jdbc;
    private String username;
    private String password;
    private Class<?> driverName;

    private List<String> datasets = new ArrayList<>();
    private Map<String, Object> options = new HashMap<>();

    RdbmsPopulatorConfigurator(URI jdbcUrl, RdbmsPopulatorService populatorService) {
        this.jdbc = jdbcUrl;
        this.populatorService = populatorService;
    }

    public RdbmsPopulatorConfigurator withUsername(String username) {
        this.username = username;
        return this;
    }

    public RdbmsPopulatorConfigurator withPassword(String password) {
        this.password = password;
        return this;
    }

    public RdbmsPopulatorConfigurator withDriver(Class<?> driver) {
        this.driverName = driver;
        return this;
    }

    /**
     * Register a new dataset for populating data.
     *
     * @param dataset
     *     to use.
     *
     * @return this instance.
     */
    public RdbmsPopulatorConfigurator usingDataSet(String dataset) {
        this.datasets.add(dataset);
        return this;
    }

    /**
     * Register new datasets for populating data.
     *
     * @param datasets
     *     to use.
     *
     * @return this instance.
     */
    public RdbmsPopulatorConfigurator usingDataSets(String... datasets) {
        this.datasets.addAll(Arrays.asList(datasets));
        return this;
    }

    /**
     * Set custom options.
     *
     * @param key
     *     name.
     * @param value
     *     of property.
     * @param elements
     *     pair key, value. Even elements are keys, odd ones values.
     *
     * @return this instance.
     */
    public RdbmsPopulatorConfigurator withOption(String key, String value, String... elements) {

        if (elements.length % 2 != 0) {
            throw new IllegalArgumentException("Extra options should be passed in form of (key, value)");
        }

        this.options.put(key, value);

        for (int i = 0; i < elements.length; i += 2) {
            this.options.put(elements[i], elements[i + 1]);
        }

        return this;
    }

    /**
     * Set custom options as map.
     *
     * @param options
     *     to set.
     *
     * @return this instance.
     */
    public RdbmsPopulatorConfigurator withOptions(Map<String, Object> options) {
        this.options.putAll(options);
        return this;
    }

    @Override
    public void execute() {
        // TODO Improve this so connect and disconnect only happens once.
        // This implies for example observing @AfterClass to disconnect and add some boolean to know that connection is already started in executeWithSchemas and clean method.
        try {
            populatorService.connect(this.jdbc, this.username, this.password, this.driverName, this.options);
            populatorService.execute(Collections.unmodifiableList(datasets));
        } finally {
            populatorService.disconnect();
        }
    }

    public void clean() {
        try {
            populatorService.connect(this.jdbc, this.username, this.password, this.driverName, this.options);
            populatorService.clean(this.datasets);
        } catch (UnsupportedOperationException e) {
            //Nothing to do just log
        } finally {
            populatorService.disconnect();
        }
    }
}
