package org.jboss.arquillian.populator.nosql.mongodb;

import com.lordofthejars.nosqlunit.mongodb.DefaultInsertionStrategy;
import com.lordofthejars.nosqlunit.mongodb.MongoDbConnectionCallback;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.jboss.arquillian.populator.core.DataSetLoader;
import org.jboss.arquillian.populator.nosql.api.NoSqlPopulatorService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Integration to NoSqlUnit MongoDb module.
 */
class MongoDbPopulatorService implements NoSqlPopulatorService<MongoDb> {

    private MongoClient mongoClient;
    private MongoDatabase database;

    @Override
    public void connect(String host, int port, String database, Map<String, Object> customOptions) {
        if (database == null) {
            database = "test";
        }

        mongoClient = new MongoClient(host, port);
        this.database = mongoClient.getDatabase(database);

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
                .map(DataSetLoader::resolve)
                .forEach((InputStream dataset) -> {
                    try {
                        mongoDBInsertionStrategy.insert(new MongoDbConnectionCallback() {
                            @Override
                            public MongoDatabase db() {
                                return database;
                            }

                            @Override
                            public MongoClient mongoClient() {
                                return mongoClient;
                            }
                        }, dataset);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                });
    }

    @Override
    public void clean() {
        final MongoIterable<String> listCollectionNames = database.listCollectionNames();

        for (String collectionName : listCollectionNames) {

            if (isNotASystemCollection(collectionName)) {

                MongoCollection dbCollection = database.getCollection(collectionName);
                // Delete ALL, No DROP
                dbCollection.deleteOne(new Document());
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
