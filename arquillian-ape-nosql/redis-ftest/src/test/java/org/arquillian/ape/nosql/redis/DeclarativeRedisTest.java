package org.arquillian.ape.nosql.redis;

import org.arquillian.ape.api.Server;
import org.arquillian.ape.api.UsingDataSet;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import redis.clients.jedis.Jedis;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@Server(host = "${arq.cube.docker.redis.ip}", port = "${arq.cube.docker.redis.6379:6379}")
public class DeclarativeRedisTest {

    @Test
    @UsingDataSet("books.json")
    public void should_populate_redis() {
        Jedis jedis = new Jedis(System.getProperty("arq.cube.docker.redis.ip"), 6379);
        final Map<String, String> fieldsOfTheHobbitBook = jedis.hgetAll("The Hobbit");

        assertThat(fieldsOfTheHobbitBook)
            .containsEntry("title", "The Hobbit")
            .containsEntry("numberOfPages", "293");
    }


}
