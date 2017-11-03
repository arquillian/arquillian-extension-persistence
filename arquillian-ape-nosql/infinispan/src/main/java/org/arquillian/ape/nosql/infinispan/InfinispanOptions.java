package org.arquillian.ape.nosql.infinispan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.arquillian.ape.core.RunnerExpressionParser;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class InfinispanOptions implements Map<String, Object> {

    static final String PROPERTIES = "properties";

    private Map<String, Object> options = new HashMap<>();

    private InfinispanOptions() {
    }

    InfinispanOptions(Map<String, Object> options) {
        this.options.putAll(options);
    }

    public static InfinispanConfigurationOptions options() {
        return new InfinispanConfigurationOptions();
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

    void configure(ConfigurationBuilder configurationBuilder) {
        if (this.options.containsKey(PROPERTIES)) {
            configurationBuilder.withProperties((Properties) this.options.get(PROPERTIES));
        }
    }

    public static InfinispanOptions from(InfinispanConfiguration infinispanConfiguration) {
        final Map<String, Object> options = new HashMap<>();

        if (! infinispanConfiguration.propertiesFile().isEmpty()) {
            File fileLocation = new File(RunnerExpressionParser.parseExpressions(infinispanConfiguration.propertiesFile()));
            try {
                options.put(PROPERTIES, loadProperties(new FileInputStream(fileLocation)));
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }

        if (! infinispanConfiguration.propertiesResource().isEmpty()) {
            String classPathLocation = RunnerExpressionParser.parseExpressions(infinispanConfiguration.propertiesResource());

            if (! classPathLocation.startsWith("/")) {
                classPathLocation = "/" + classPathLocation;
            }

            options.put(PROPERTIES, loadProperties(Class.class.getResourceAsStream(classPathLocation)));

        }

        return new InfinispanOptions(options);

    }

    private static Properties loadProperties(InputStream inputStream) {
        final Properties properties = new Properties();
        try {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static class InfinispanConfigurationOptions {
        private InfinispanOptions infinispanOptions = new InfinispanOptions();

        private InfinispanConfigurationOptions() {
        }

        public InfinispanConfigurationOptions properties(Properties properties) {
            this.infinispanOptions.put(PROPERTIES, properties);
            return this;
        }

        public InfinispanOptions build() {
            return this.infinispanOptions;
        }
    }
}
