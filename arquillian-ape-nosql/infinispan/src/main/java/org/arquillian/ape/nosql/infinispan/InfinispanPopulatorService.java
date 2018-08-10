package org.arquillian.ape.nosql.infinispan;

import com.lordofthejars.nosqlunit.infinispan.DefaultInfinispanInsertionStrategy;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.arquillian.ape.core.DataSetLoader;
import org.arquillian.ape.nosql.NoSqlPopulatorService;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.BasicCacheContainer;

class InfinispanPopulatorService implements NoSqlPopulatorService<Infinispan> {

    private BasicCache<Object, Object> infinispanBasicCache;

    @Override
    public void connect(Object embeddedConnection, String database, Map<String, Object> customOptions) {

        BasicCacheContainer cacheContainer = (BasicCacheContainer) embeddedConnection;

        if (database == null) {
            infinispanBasicCache = cacheContainer.getCache();
        } else {
            infinispanBasicCache = cacheContainer.getCache(database);
        }

    }

    @Override
    public void connect(String host, int port, String database, Map<String, Object> customOptions) {

        final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        configurationBuilder.addServer()
            .host(host)
            .port(port);

        final InfinispanOptions infinispanOptions = new InfinispanOptions(customOptions);
        infinispanOptions.configure(configurationBuilder);

        final RemoteCacheManager remoteCacheManager = new RemoteCacheManager(configurationBuilder.build());

        if (database == null) {
            infinispanBasicCache = remoteCacheManager.getCache();
        } else {
            infinispanBasicCache = remoteCacheManager.getCache(database);
        }
    }

    @Override
    public void connect(URI uri, String database, Map<String, Object> customOptions) {
        this.connect(uri.getHost(), uri.getPort(), database, customOptions);
    }


    @Override
    public void disconnect() {
        if (infinispanBasicCache != null) {
            infinispanBasicCache.stop();
        }
    }

    @Override
    public void execute(List<String> resources) {
        final DefaultInfinispanInsertionStrategy defaultInfinispanInsertionStrategy = new DefaultInfinispanInsertionStrategy();

        resources.stream()
            .map(DataSetLoader::resolve)
            .forEach((InputStream dataset) -> {
                try {
                    defaultInfinispanInsertionStrategy.insert(() -> infinispanBasicCache, dataset);
                } catch (Throwable e) {
                    throw new IllegalStateException(String.format("Error inserting %s resources.", resources), e);
                }
            });
    }

    @Override
    public void clean() {

        if (infinispanBasicCache != null) {
            this.infinispanBasicCache.clear();
        }
    }

    @Override
    public Class<Infinispan> getPopulatorAnnotation() {
        return Infinispan.class;
    }
}
