package org.arquillian.ape.nosql.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.cluster.ClusterManager;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import com.lordofthejars.nosqlunit.couchbase.DefaultCouchbaseInsertionStrategy;
import org.arquillian.ape.core.DataSetLoader;
import org.arquillian.ape.nosql.NoSqlPopulatorService;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

class CouchbasePopulatorService implements NoSqlPopulatorService<Couchbase> {

    private CouchbaseCluster couchbaseCluster;
    private Bucket bucket;

    @Override
    public void connect(String host, int port, String database, Map<String, Object> customOptions) {
        if (database == null) {
            database = "default";
        }

        this.couchbaseCluster = CouchbaseCluster.create(createCouchbaseClusterUri(host, port));

        connectToBucket(database, customOptions);

    }

    @Override
    public void connect(URI uri, String database, Map<String, Object> customOptions) {
        if (database == null) {
            database = "default";
        }

        this.couchbaseCluster = CouchbaseCluster.create(uri.toString());
        connectToBucket(database, customOptions);
    }

    private void connectToBucket(String database, Map<String, Object> customOptions) {
        if (isCreationOfBucketEnabled(customOptions)) {
            createBucket(database, customOptions);
        }

        if (isBucketPasswordSet(customOptions)) {
            this.bucket = couchbaseCluster.openBucket(database, (String) customOptions.get(CouchbaseOptions.BUCKET_PASSWORD));
        } else {
            this.bucket = couchbaseCluster.openBucket(database);
        }
    }

    private String createCouchbaseClusterUri(String host, int port) {
        if (port > 0) {
            return host + ":" + port;
        } else {
            return host;
        }
    }

    private boolean isBucketPasswordSet(Map<String, Object> customOptions) {
        return customOptions.containsKey(CouchbaseOptions.BUCKET_PASSWORD);
    }

    private boolean isCreationOfBucketEnabled(Map<String, Object> customOptions) {
        return customOptions.containsKey(CouchbaseOptions.CREATE_BUCKET);
    }

    private void createBucket(String database, Map<String, Object> customOptions) {
        final ClusterManager clusterManager = couchbaseCluster
                .clusterManager((String) customOptions.get(CouchbaseOptions.CLUSTER_USERNAME), (String) customOptions.get(CouchbaseOptions.CLUSTER_PASSWORD));

        if (! clusterManager.hasBucket(database)) {
            // Create Bucket
            final DefaultBucketSettings.Builder bucketBuilder = DefaultBucketSettings.builder()
                    .enableFlush(true)
                    .name(database);

            if (isBucketPasswordSet(customOptions)) {
                bucketBuilder.password((String) customOptions.get(CouchbaseOptions.BUCKET_PASSWORD));
            }


            clusterManager.insertBucket(bucketBuilder.build());

            await()
                    .atMost(60, TimeUnit.SECONDS)
                    .until(() -> clusterManager.hasBucket(database));

        }
    }

    @Override
    public void disconnect() {
        if (bucket != null) {
            bucket.close();
        }
    }

    @Override
    public void execute(List<String> resources) {
        final DefaultCouchbaseInsertionStrategy couchbaseInsertionStrategy = new DefaultCouchbaseInsertionStrategy();

        resources.stream()
                .map(DataSetLoader::resolve)
                .forEach((InputStream dataset) -> {
                    try {
                        couchbaseInsertionStrategy.insert(() -> bucket, dataset);
                    } catch (Throwable e) {
                        throw new IllegalStateException(e);
                    }
                });
    }

    @Override
    public void clean() {
        bucket.bucketManager().flush();
    }

    @Override
    public Class<Couchbase> getPopulatorAnnotation() {
        return Couchbase.class;
    }
}
