package org.arquillian.ape.nosql.redis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.arquillian.ape.core.RunnerExpressionParser;

public class RedisOptions implements Map<String, Object> {

    static final String SSL = "ssl";

    private Map<String, Object> options = new HashMap<>();

    private RedisOptions() {
    }

    RedisOptions(Map<String, Object> options) {
        this.options.putAll(options);
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

    public boolean isSsl() {
        return this.options.containsKey(SSL) && (Boolean) this.options.get(SSL);
    }

    public static RedisOptions from(RedisConfiguration redisConfiguration) {
        final Map<String, Object> options = new HashMap<>();

        if (! redisConfiguration.ssl().isEmpty()) {
            options.put(SSL,Boolean.parseBoolean(RunnerExpressionParser.parseExpressions(redisConfiguration.ssl())));
        }

        return new RedisOptions(options);
    }

    public static class RedisConfigurationOptions{

        private RedisOptions redisOptions = new RedisOptions();

        private RedisConfigurationOptions() {
        }

        public RedisConfigurationOptions ssl(boolean ssl) {
            this.redisOptions.put(SSL, ssl);
            return this;
        }

        public RedisOptions build() {
            return this.redisOptions;
        }

    }

}
