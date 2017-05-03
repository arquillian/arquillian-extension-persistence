package org.arquillian.ape.nosql.couchdb;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.ektorp.http.StdHttpClient;

public class CouchDbOptions implements Map<String, Object> {

    static final String USERNAME = "username";
    static final String PASSWORD = "password";
    static final String CACHING = "caching";
    static final String ENABLE_SSL = "enableSsl";
    static final String RELAXED_SSL = "relaxedSsl";
    static final String SSL_FACTORY_SOCKET = "sslFactorySocket";

    private Map<String, Object> options = new HashMap<>();

    private CouchDbOptions() {
    }

    CouchDbOptions(Map<String, Object> options) {
        this.options.putAll(options);
    }

    public static CouchDbConfigurationOptions options() {
        return new CouchDbConfigurationOptions();
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

    void configure(StdHttpClient.Builder httpBuilder) {
        if (this.options.containsKey(USERNAME)) {
            httpBuilder.username((String) this.options.get(USERNAME));
        }

        if (this.options.containsKey(PASSWORD)) {
            httpBuilder.password((String) this.options.get(PASSWORD));
        }

        if (this.options.containsKey(CACHING)) {
            httpBuilder.caching((Boolean) this.options.get(CACHING));
        } else {
            httpBuilder.caching(true);
        }

        if (this.options.containsKey(ENABLE_SSL)) {
            httpBuilder.enableSSL((Boolean) this.options.get(ENABLE_SSL));
        }

        if (this.options.containsKey(RELAXED_SSL)) {
            httpBuilder.relaxedSSLSettings((Boolean) this.options.get(RELAXED_SSL));
        }

        if (this.options.containsKey(SSL_FACTORY_SOCKET)) {
            httpBuilder.sslSocketFactory((org.apache.http.conn.ssl.SSLSocketFactory) this.options.get(SSL_FACTORY_SOCKET));
        }
    }

    public static class CouchDbConfigurationOptions {
        private CouchDbOptions couchDbOptions = new CouchDbOptions();

        private CouchDbConfigurationOptions() {
        }

        public CouchDbConfigurationOptions username(String username) {
            this.couchDbOptions.put(USERNAME, username);
            return this;
        }

        public CouchDbConfigurationOptions password(String password) {
            this.couchDbOptions.put(PASSWORD, password);
            return this;
        }

        public CouchDbConfigurationOptions caching(Boolean caching) {
            this.couchDbOptions.put(CACHING, caching);
            return this;
        }

        public CouchDbConfigurationOptions enableSsl(Boolean enableSsl) {
            this.couchDbOptions.put(ENABLE_SSL, enableSsl);
            return this;
        }

        public CouchDbConfigurationOptions relaxedSsl(Boolean relaxedSsl) {
            this.couchDbOptions.put(RELAXED_SSL, relaxedSsl);
            return this;
        }

        public CouchDbConfigurationOptions sslFactorySocket(org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory) {
            this.couchDbOptions.put(SSL_FACTORY_SOCKET, sslSocketFactory);
            return this;
        }

        public CouchDbOptions build() {
            return this.couchDbOptions;
        }
    }
}
