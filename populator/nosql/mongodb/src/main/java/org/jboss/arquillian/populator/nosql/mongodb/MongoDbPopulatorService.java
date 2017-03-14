package org.jboss.arquillian.populator.nosql.mongodb;

import com.lordofthejars.nosqlunit.mongodb.DefaultInsertionStrategy;
import com.mongodb.*;
import org.jboss.arquillian.populator.nosql.api.NoSqlPopulatorService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Integration to NoSqlUnit MongoDb module.
 */
public class MongoDbPopulatorService implements NoSqlPopulatorService<MongoDb> {

    private Mongo mongoClient;
    private DB database;

    @Override
    public void connect(String host, int port, String database, Map<String, Object> customOptions) {
        if (database == null) {
            database = "test";
        }

        mongoClient = new MongoClient(host, port);
        this.database = mongoClient.getDB(database);

    }

    @Override
    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Override
    public void execute(List<String> resources) {
        final DefaultInsertionStrategy mongoDBInsertionStrategy = new DefaultInsertionStrategy();

        resources.stream()
                .map(resource -> MongoDbPopulatorService.class.getResourceAsStream(resource))
                .forEach((InputStream dataset) -> {
                    try {
                        mongoDBInsertionStrategy.insert(() -> database, dataset);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                });
    }

    @Override
    public void clean() {
        Set<String> collectionaNames = database.getCollectionNames();

        for (String collectionName : collectionaNames) {

            if (isNotASystemCollection(collectionName)) {

                DBCollection dbCollection = database.getCollection(collectionName);
                // Delete ALL, No DROP
                dbCollection.remove(new BasicDBObject(0));
            }
        }
    }

    private boolean isNotASystemCollection(String collectionName) {
        return !collectionName.startsWith("system.");
    }


    @Override
    public Class<MongoDb> getPopulatorAnnotation() {
        return MongoDb.class;
    }
}
