package org.arquillian.ape.nosql.couchdb;

import com.lordofthejars.nosqlunit.couchdb.DefaultCouchDbInsertionStrategy;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.arquillian.ape.core.DataSetLoader;
import org.arquillian.ape.nosql.NoSqlPopulatorService;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.RestTemplate;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

class CouchDbPopulatorService implements NoSqlPopulatorService<CouchDb> {

    private CouchDbConnector couchDbConnector;

    @Override
    public void connect(Object embeddedConnection, String database, Map<String, Object> customOptions) {
        this.couchDbConnector = (CouchDbConnector) embeddedConnection;
    }

    @Override
    public void connect(String host, int port, String database, Map<String, Object> customOptions) {
        this.couchDbConnector = couchDbConnector(createCouchDbClusterUri(host, port), database, customOptions);
    }

    @Override
    public void connect(URI uri, String database, Map<String, Object> customOptions) {
        this.couchDbConnector = couchDbConnector(uri.toString(), database, customOptions);
    }

    private String createCouchDbClusterUri(String host, int port) {
        if (port > 0) {
            return "http://" + host + ":" + port;
        } else {
            return "http://" + host;
        }
    }

    public CouchDbConnector couchDbConnector(String uri, String database, Map<String, Object> configuration) {
        StdHttpClient.Builder httpBuilder = couchDbHttpClient(uri, configuration);
        return couchDbConnector(database, httpBuilder);
    }

    private CouchDbConnector couchDbConnector(String database,
        StdHttpClient.Builder httpBuilder) {
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpBuilder.build());
        return dbInstance.createConnector(database, true);
    }

    private StdHttpClient.Builder couchDbHttpClient(String uri, Map<String, Object> configuration) {
        StdHttpClient.Builder httpBuilder = new StdHttpClient.Builder();
        try {
            httpBuilder.url(uri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        CouchDbOptions couchDbOptions = new CouchDbOptions(configuration);
        couchDbOptions.configure(httpBuilder);
        return httpBuilder;
    }

    @Override
    public void disconnect() {
        if (couchDbConnector != null) {
            couchDbConnector.getConnection().shutdown();
        }
    }

    @Override
    public void execute(List<String> resources) {
        final DefaultCouchDbInsertionStrategy defaultCouchDbInsertionStrategy = new DefaultCouchDbInsertionStrategy();

        resources.stream()
            .map(DataSetLoader::resolve)
            .forEach((InputStream dataset) -> {
                try {
                    defaultCouchDbInsertionStrategy.insert(() -> couchDbConnector, dataset);
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }
            });
    }

    @Override
    public void clean() {

        if (couchDbConnector != null) {

            HttpClient connection = couchDbConnector.getConnection();
            RestTemplate restTemplate = new RestTemplate(connection);
            restTemplate.delete(couchDbConnector.path());

            couchDbConnector.createDatabaseIfNotExists();
        }
    }

    @Override
    public Class<CouchDb> getPopulatorAnnotation() {
        return CouchDb.class;
    }
}
