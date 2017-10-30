package org.arquillian.ape.nosql.redis;

import redis.clients.jedis.Jedis;

public class JedisClientFactory {

    public static Jedis createJedisInstance() {
        int redisPort = Integer.parseInt(System.getProperty("arq.cube.docker.redis_3_2_6.port.6379"));
        final String redisHost = System.getProperty("arq.cube.docker.redis_3_2_6.ip");

        return new Jedis(redisHost, redisPort);
    }

}
