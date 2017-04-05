package org.arquillian.ape.rdbms.dbunit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DbUnitOptions implements Map<String, Object> {

    static String SCHEMA = "SCHEMA";

    private Map<String, Object> options = new HashMap<>();

    private DbUnitOptions() {
    }

    public static DbUnitConfigurationOptions options() {
        return new DbUnitConfigurationOptions();
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

    public static class DbUnitConfigurationOptions {

        private DbUnitOptions dbUnitOptions = new DbUnitOptions();

        private DbUnitConfigurationOptions() {
        }

        public DbUnitConfigurationOptions schema(String schema) {
            dbUnitOptions.put(DbUnitOptions.SCHEMA, schema);

            return this;
        }

        public DbUnitOptions build() {
            return dbUnitOptions;
        }

    }

}
