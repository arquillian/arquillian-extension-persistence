package org.arquillian.ape.nosql.vault;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.arquillian.ape.api.MetadataExtractor;
import org.arquillian.ape.core.RunnerExpressionParser;

public class VaultOptions implements Map<String, Object> {

    static final String TOKEN = "token";
    static final String SSL_PEM_UTF_8 = "sslPemUtf8";
    static final String SSL_PEM_FILE = "sslPemFile";
    static final String SSL_PEM_RESOURCE = "sslPemResource";
    static final String SSL_VERIFY = "sslVerify";
    static final String OPEN_TIMEOUT = "openTimeout";
    static final String READ_TIMEOUT = "readTimeout";

    private Map<String, Object> options = new HashMap<>();

    private VaultOptions() {
    }

    VaultOptions(Map<String, Object> options) {
        this.options.putAll(options);
    }

    public static VaultConfigurationOptions options() {
        return new VaultConfigurationOptions();
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

    void configure(VaultConfig vaultConfig) {
        if (this.options.containsKey(TOKEN)) {
            vaultConfig.token((String) this.options.get(TOKEN));
        }

        if (this.options.containsKey(SSL_PEM_FILE)) {
            try {
                vaultConfig.sslPemFile((File) this.options.get(SSL_PEM_FILE));
            } catch (VaultException e) {
                throw new IllegalArgumentException(e);
            }
        }

        if (this.options.containsKey(SSL_PEM_RESOURCE)) {
            try {
                vaultConfig.sslPemResource((String) this.options.get(SSL_PEM_RESOURCE));
            } catch (VaultException e) {
                throw new IllegalArgumentException(e);
            }
        }

        if (this.options.containsKey(SSL_PEM_UTF_8)) {
            vaultConfig.sslPemUTF8((String) this.options.get(SSL_PEM_UTF_8));
        }

        if (this.options.containsKey(SSL_VERIFY)) {
            vaultConfig.sslVerify((Boolean) this.options.get(SSL_VERIFY));
        }

        if (this.options.containsKey(OPEN_TIMEOUT)) {
            vaultConfig.openTimeout((Integer) this.options.get(OPEN_TIMEOUT));
        }

        if (this.options.containsKey(READ_TIMEOUT)) {
            vaultConfig.readTimeout((Integer) this.options.get(READ_TIMEOUT));
        }

    }

    public static VaultOptions from(VaultConfiguration vaultConfiguration) {
        final Map<String, Object> options = new HashMap<>();
        options.put(TOKEN, RunnerExpressionParser.parseExpressions(vaultConfiguration.token()));

        if (! vaultConfiguration.sslPemUtf8().isEmpty()) {
            options.put(SSL_PEM_UTF_8, RunnerExpressionParser.parseExpressions(vaultConfiguration.sslPemUtf8()));
        }

        if (! vaultConfiguration.sslPemFile().isEmpty()) {
            options.put(SSL_PEM_UTF_8, new File(RunnerExpressionParser.parseExpressions(vaultConfiguration.sslPemFile())));
        }

        if (! vaultConfiguration.sslPemResource().isEmpty()) {
            options.put(SSL_PEM_RESOURCE, RunnerExpressionParser.parseExpressions(vaultConfiguration.sslPemResource()));
        }

        if (! vaultConfiguration.sslVerify().isEmpty()) {
            options.put(SSL_VERIFY, Boolean.parseBoolean(RunnerExpressionParser.parseExpressions(vaultConfiguration.sslVerify())));
        }

        if (! vaultConfiguration.openTimeout().isEmpty()) {
            options.put(OPEN_TIMEOUT, Integer.parseInt(RunnerExpressionParser.parseExpressions(vaultConfiguration.openTimeout())));
        }

        if (! vaultConfiguration.readTimeout().isEmpty()) {
            options.put(READ_TIMEOUT, Integer.parseInt(RunnerExpressionParser.parseExpressions(vaultConfiguration.readTimeout())));
        }

        return new VaultOptions(options);
    }

    public static class VaultConfigurationOptions {
        private VaultOptions vaultOptions = new VaultOptions();

        private VaultConfigurationOptions() {
        }

        public VaultConfigurationOptions token(String token) {
            this.vaultOptions.put(TOKEN, token);
            return this;
        }

        public VaultConfigurationOptions sslPemUtf8(String sslPemUtf8) {
            this.vaultOptions.put(SSL_PEM_UTF_8, sslPemUtf8);
            return this;
        }

        public VaultConfigurationOptions sslPemFile(File sslPemFile) {
            this.vaultOptions.put(SSL_PEM_FILE, sslPemFile);
            return this;
        }

        public VaultConfigurationOptions sslPemResource(String classpathResource) {
            this.vaultOptions.put(SSL_PEM_RESOURCE, classpathResource);
            return this;
        }

        public VaultConfigurationOptions sslVerify(Boolean sslVerify) {
            this.vaultOptions.put(SSL_VERIFY,sslVerify);
            return this;
        }

        public VaultConfigurationOptions openTimeout(Integer openTimeout) {
            this.vaultOptions.put(OPEN_TIMEOUT, openTimeout);
            return this;
        }

        public VaultConfigurationOptions readTimeout(Integer readTimeout) {
            this.vaultOptions.put(READ_TIMEOUT, readTimeout);
            return this;
        }

        public VaultOptions build() {
            return this.vaultOptions;
        }

    }

}
