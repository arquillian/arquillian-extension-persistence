package org.arquillian.ape.rdbms.core;

import org.arquillian.ape.spi.PopulatorService;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Extends the populator service for Rdbms.
 *
 * @param <T>
 */
public interface RdbmsPopulatorService<T> extends PopulatorService<T> {
    /**
     * Methods called to connect to the backend.
     *
     * @param jdbc          uri to coonnect
     * @param customOptions to use for connection.
     */
    void connect(URI jdbc, String username, String password, Class<?> driver, Map<String, Object> customOptions);

    /**
     * Method called to disconnect from the backend.
     */
    void disconnect();

    /**
     * Method executed to populate model data.
     *
     * @param resources used to populate. The meaning of this string depends on implementators. Some might treat this as directory, others like specific files (being in classpath or not), ...
     */
    void execute(List<String> resources);

    /**
     * Method executed to clean model data. Notice that this operation is not mandatory and Unsupported Operation Exception can be thrown.
     *
     * @see UnsupportedOperationException which is called when backend does not implement clean operation.
     */
    void clean(List<String> resources);
}
