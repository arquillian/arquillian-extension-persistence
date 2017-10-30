package org.arquillian.ape.nosql.redis;

import java.util.Map;
import redis.clients.jedis.Jedis;

public class BookService {

    private Jedis jedis;

    public BookService() {
        int redisPort = Integer.parseInt(System.getProperty("arq.cube.docker.redis_3_2_6.port.6379"));
        final String redisHost = System.getProperty("arq.cube.docker.redis_3_2_6.ip");

        this.jedis = new Jedis(redisHost, redisPort);
    }

    public Map<String, String> findBookByTitle(String title) {
        return this.jedis.hgetAll("The Hobbit");
    }

}
