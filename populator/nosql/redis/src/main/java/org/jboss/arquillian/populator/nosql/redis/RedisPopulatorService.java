package org.jboss.arquillian.populator.nosql.redis;

import com.lordofthejars.nosqlunit.redis.DefaultRedisInsertionStrategy;
import com.lordofthejars.nosqlunit.redis.RedisConnectionCallback;
import org.jboss.arquillian.populator.nosql.api.NoSqlPopulatorService;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Integration to NoSqlUnit Redis module.
 */
class RedisPopulatorService implements NoSqlPopulatorService<Redis> {

    private Jedis jedis;

    @Override
    public void connect(String host, int port, String database, Map<String, Object> customOptions) {
        jedis = new Jedis(host, port);
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
                .map(RedisPopulatorService.class::getResourceAsStream)
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
