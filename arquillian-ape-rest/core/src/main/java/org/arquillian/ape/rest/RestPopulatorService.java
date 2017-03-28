package org.arquillian.ape.rest;

import org.arquillian.ape.spi.PopulatorService;

import java.util.List;
import java.util.Map;

public interface RestPopulatorService<T> extends PopulatorService<T> {

    /**
     * Method executed to send content to a service.
     *
     * @param resources used to populate. The meaning of this string depends on implementators. Some might treat this as directory, others like specific files (being in classpath or not), ...
     * @param variables value to replace in script.
     */
    void execute(List<String> resources, Map<String, String> variables);

    /**
     * Method executed to send content to a service changing host and port specified in dataset to given one.
     *
     * @param host      to change on dataset fields where host is used.
     * @param bindPort  to change on dataset fields where port is used.
     * @param resources used to populate. The meaning of this string depends on implementators. Some might treat this as directory, others like specific files (being in classpath or not), ...
     * @param variables value to replace in script.
     */
    void execute(String host, int bindPort, List<String> resources, Map<String, String> variables);

    /**
     * Method executed to clean service.
     *
     * @param resources used to populate. The meaning of this string depends on implementators. Some might treat this as directory, others like specific files (being in classpath or not), ...
     * @param variables value to replace in script.
     */
    void clean(List<String> resources, Map<String, String> variables);

    /**
     * Method executed to clean service changing host and port specified in dataset to given one.
     *
     * @param host      to change on dataset fields where host is used.
     * @param bindPort  to change on dataset fields where port is used.
     * @param resources used to populate. The meaning of this string depends on implementators. Some might treat this as directory, others like specific files (being in classpath or not), ...
     * @param variables value to replace in script.
     */
    void clean(String host, int bindPort, List<String> resources, Map<String, String> variables);

}
