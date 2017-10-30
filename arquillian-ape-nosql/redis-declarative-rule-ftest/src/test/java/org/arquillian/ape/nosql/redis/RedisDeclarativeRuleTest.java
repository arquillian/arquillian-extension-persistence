package org.arquillian.ape.nosql.redis;

import java.util.Map;
import org.arquillian.ape.api.Server;
import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.junit.rule.DeclarativeArquillianPersistenceRule;
import org.arquillian.cube.docker.junit.rule.ContainerDslRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.assertj.core.api.Assertions.assertThat;

@Server(host = "${arq.cube.docker.redis_3_2_6.ip}", port = "${arq.cube.docker.redis_3_2_6.port.6379:6379}")
public class RedisDeclarativeRuleTest {

    @ClassRule
    public static ContainerDslRule redis = new ContainerDslRule("redis:3.2.6")
        .withPortBinding(6379);

    @Rule
    public DeclarativeArquillianPersistenceRule declarativeArquillianPersistenceRule = new DeclarativeArquillianPersistenceRule();


    @Test
    @UsingDataSet("books.json")
    public void should_populate_redis() {

        final Jedis jedis = JedisClientFactory.createJedisInstance();
        final Map<String, String> fieldsOfTheHobbitBook = jedis.hgetAll("The Hobbit");

        assertThat(fieldsOfTheHobbitBook)
            .containsEntry("title", "The Hobbit")
            .containsEntry("numberOfPages", "293");
    }


}
