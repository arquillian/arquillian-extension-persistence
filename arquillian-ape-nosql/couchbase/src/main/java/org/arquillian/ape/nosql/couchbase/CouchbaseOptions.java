package org.arquillian.ape.nosql.couchbase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.arquillian.ape.core.RunnerExpressionParser;

public class CouchbaseOptions implements Map<String, Object> {

    static String BUCKET_PASSWORD = "BUCKET_PASSWORD";
    static String CREATE_BUCKET = "CREATE_BUCKET";
    static String CLUSTER_USERNAME = "CLUSTER_USERNAME";
    static String CLUSTER_PASSWORD = "CLUSTER_PASSWORD";

    private Map<String, Object> options = new HashMap<>();

    private CouchbaseOptions() {
    }

    protected CouchbaseOptions(Map<String, Object> options) {
        this.options.putAll(options);
    }

    public static CouchbaseConfigurationOptions options() {
        return new CouchbaseConfigurationOptions();
    }

    @Override
    public int size() {
        return options.size();
    }

    @Override
    public boolean isEmpty() {
        return options.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return options.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return options.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return options.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return options.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return options.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        options.putAll(m);
    }

    @Override
    public void clear() {
        options.clear();
    }

    @Override
    public Set<String> keySet() {
        return options.keySet();
    }

    @Override
    public Collection<Object> values() {
        return options.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return options.entrySet();
    }

    public static CouchbaseOptions from(CouchbaseConfiguration couchbaseConfiguration) {

        final Map<String, Object> options = new HashMap<>();

        if (! couchbaseConfiguration.bucketPassword().isEmpty()) {
            options.put(BUCKET_PASSWORD, RunnerExpressionParser.parseExpressions(couchbaseConfiguration.bucketPassword()));
        }

        if (! couchbaseConfiguration.createBucker().isEmpty()) {
            options.put(CREATE_BUCKET, Boolean.parseBoolean(RunnerExpressionParser.parseExpressions(couchbaseConfiguration.createBucker())));
        }

        if (! couchbaseConfiguration.clusterUsername().isEmpty()) {
            options.put(CLUSTER_USERNAME, RunnerExpressionParser.parseExpressions(couchbaseConfiguration.clusterUsername()));
        }

        if (! couchbaseConfiguration.clusterPassword().isEmpty()) {
            options.put(CLUSTER_PASSWORD, RunnerExpressionParser.parseExpressions(couchbaseConfiguration.clusterPassword()));
        }

        return new CouchbaseOptions(options);
    }

    public static class CouchbaseConfigurationOptions {
        private CouchbaseOptions couchbaseOptions = new CouchbaseOptions();

        private CouchbaseConfigurationOptions() {
        }

        public CouchbaseConfigurationOptions bucketPassword(String password) {
            couchbaseOptions.put(CouchbaseOptions.BUCKET_PASSWORD, password);
            return this;
        }

        public CouchbaseConfigurationOptions createBucket(String clusterUsername, String clusterPassword) {
            couchbaseOptions.put(CouchbaseOptions.CREATE_BUCKET, true);
            couchbaseOptions.put(CouchbaseOptions.CLUSTER_USERNAME, clusterUsername);
            couchbaseOptions.put(CouchbaseOptions.CLUSTER_PASSWORD, clusterPassword);

            return this;
        }

        public CouchbaseOptions build() {
            return couchbaseOptions;
        }
    }
}
