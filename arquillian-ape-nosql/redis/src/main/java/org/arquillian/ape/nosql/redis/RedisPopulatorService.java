package org.arquillian.ape.nosql.redis;

import com.lordofthejars.nosqlunit.redis.DefaultRedisInsertionStrategy;
import com.lordofthejars.nosqlunit.redis.RedisConnectionCallback;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.arquillian.ape.core.DataSetLoader;
import org.arquillian.ape.nosql.NoSqlPopulatorService;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.Jedis;

/**
 * Integration to NoSqlUnit Redis module.
 */
class RedisPopulatorService implements NoSqlPopulatorService<Redis> {

    private Jedis jedis;

    @Override
    public void connect(Object embeddedConnection, String database, Map<String, Object> customOptions) {
        this.jedis = (Jedis) embeddedConnection;
        this.jedis.connect();
    }

    @Override
    public void connect(String host, int port, String database, Map<String, Object> customOptions) {
        RedisOptions redisOptions = new RedisOptions(customOptions);

        jedis = new Jedis(host, port, redisOptions.isSsl());
        jedis.connect();
    }

    @Override
    public void connect(URI uri, String database, Map<String, Object> customOptions) {
        // Jedis does not offer the ssl option in URI because it is explicitly in protocol.
        jedis = new Jedis(uri);
        jedis.connect();
    }

    @Override
    public void disconnect() {
        if (jedis != null) {
            jedis.disconnect();
        }
    }

    @Override
    public void execute(List<String> resources) {
        final DefaultRedisInsertionStrategy redisInsertionStrategy = new DefaultRedisInsertionStrategy();
        final RedisConnectionCallback connection = new RedisConnectionCallback() {
            @Override
            public BinaryJedisCommands insertionJedis() {
                return jedis;
            }

            @Override
            public Jedis getActiveJedis(byte[] bytes) {
                return jedis;
            }

            @Override
            public Collection<Jedis> getAllJedis() {
                return Collections.singletonList(jedis);
            }
        };

        resources.stream()
            .map(DataSetLoader::resolve)
            .forEach(dataset -> {
                try {
                    redisInsertionStrategy.insert(connection, dataset);
                } catch (Throwable throwable) {
                    throw new IllegalStateException(throwable);
                }
            });
    }

    @Override
    public void clean() {
        this.jedis.flushDB();
    }

    @Override
    public Class<Redis> getPopulatorAnnotation() {
        return Redis.class;
    }
}
