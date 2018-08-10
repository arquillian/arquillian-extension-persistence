package org.arquillian.ape.nosql;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.arquillian.ape.spi.PopulatorService;

/**
 * Extends the populator service for NoSql services. Each NoSql database should implement this interface, for example one
 * for MongoDb, another one for Redis, ...
 * You can think about this interface as wrapper to real connection against service.
 */
public interface NoSqlPopulatorService<T> extends PopulatorService<T> {

    /**
     * This method is used to set directly the connection to the service.
     *
     * Useful for embedded inmemory connections where there is no network implied or for mocking.
     *
     * @param embeddedConnection to use.
     * @param database to use.
     * @param customOptions to use for connection.
     */
    void connect(Object embeddedConnection, String database, Map<String, Object> customOptions);

    /**
     * Methods called to connect to the backend.
     *
     * @param host
     *     to connect.
     * @param bindPort
     *     to connect.
     * @param database
     *     to use.
     * @param customOptions
     *     to use for connection.
     */
    void connect(String host, int bindPort, String database, Map<String, Object> customOptions);

    /**
     * Methods called to connect to the backend.
     *
     * @param uri
     *     to use
     * @param database
     *     to connect
     * @param customOptions
     *     to use in connection.
     */
    void connect(URI uri, String database, Map<String, Object> customOptions);

    /**
     * Method called to disconnect from the backend.
     */
    void disconnect();

    /**
     * Method executed to populate model data.
     *
     * @param resources
     *     used to populate. The meaning of this string depends on implementators. Some might treat this as directory,
     *     others like specific files (being in classpath or not), ...
     */
    void execute(List<String> resources);

    /**
     * Method executed to clean model data. Notice that this operation is not mandatory and Unsupported Operation
     * Exception can be thrown.
     *
     * @see UnsupportedOperationException which is called when backend does not implement clean operation.
     */
    void clean();
}
