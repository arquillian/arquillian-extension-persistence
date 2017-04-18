package org.arquillian.ape.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.arquillian.ape.spi.Populator;

public class RestPopulatorConfigurator implements Populator.PopulatorConfigurator {

    private String host;
    private int bindPort;
    private RestPopulatorService populatorService;
    private List<String> datasets = new ArrayList<>();
    private Map<String, String> variables = new HashMap<>();

    private URI uri;

    private boolean urlOverride = true;

    RestPopulatorConfigurator(URI uri, RestPopulatorService populatorService) {
        this.uri = uri;
        this.populatorService = populatorService;
    }

    RestPopulatorConfigurator(String host, int bindPort, RestPopulatorService populatorService) {
        this.host = host;
        this.bindPort = bindPort;
        this.populatorService = populatorService;
    }

    public RestPopulatorConfigurator avoidUrlOverride() {
        this.urlOverride = false;
        return this;
    }

    public RestPopulatorConfigurator usingDataSets(String... datasets) {
        this.datasets.addAll(Arrays.asList(datasets));
        return this;
    }

    /**
     * Set variables to be used in script.
     *
     * @param variables
     *     map.
     *
     * @return this instance.
     */
    public RestPopulatorConfigurator withVariables(Map<String, String> variables) {
        this.variables.putAll(variables);
        return this;
    }

    /**
     * Set variables to be used in script.
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
    public RestPopulatorConfigurator withVariables(String key, String value, String... elements) {

        if (elements.length % 2 != 0) {
            throw new IllegalArgumentException("Variables should be passed in form of (key, value)");
        }

        this.variables.put(key, value);

        for (int i = 0; i < elements.length; i += 2) {
            this.variables.put(elements[i], elements[i + 1]);
        }

        return this;
    }

    @Override
    public void execute() {
        if (urlOverride) {
            // TODO should runners manage uri internally? Yes let's do it in next alpha since it is an internal change
            if (uri != null) {
                this.host = uri.getHost();
                this.bindPort = uri.getPort();
            }

            this.populatorService.execute(host, bindPort, Collections.unmodifiableList(this.datasets),
                Collections.unmodifiableMap(variables));
        } else {
            this.populatorService.execute(Collections.unmodifiableList(this.datasets),
                Collections.unmodifiableMap(variables));
        }
    }

    @Override
    public void clean() {
        if (urlOverride) {

            if (uri != null) {
                this.host = uri.getHost();
                this.bindPort = uri.getPort();
            }

            this.populatorService.clean(this.host, this.bindPort, Collections.unmodifiableList(this.datasets),
                Collections.unmodifiableMap(variables));
        } else {
            this.populatorService.clean(Collections.unmodifiableList(this.datasets),
                Collections.unmodifiableMap(variables));
        }
    }
}
