package org.arquillian.cube.populator.redis;

import org.arquillian.cube.HostIp;
import org.arquillian.cube.HostPort;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.populator.nosql.api.NoSqlPopulator;
import org.jboss.arquillian.populator.nosql.redis.Redis;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import redis.clients.jedis.Jedis;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class RedisTest {

   @HostIp
   private String hostIp;

   @HostPort(containerName = "redis", value = 6379)
   int port;

   @ArquillianResource
   @Redis
   NoSqlPopulator populator;

   @Test
   public void should_populate_mongodb() {
      populator.forServer(hostIp, port)
              .usingDataSet("/books.json")
              .execute();

      Jedis jedis = new Jedis(hostIp, port);
      final Map<String, String> fieldsOfTheHobbitBook = jedis.hgetAll("The Hobbit");

      assertThat(fieldsOfTheHobbitBook)
              .containsEntry("title", "The Hobbit")
              .containsEntry("numberOfPages", "293");

   }

   @After
   public void cleanDatabase() {
      populator.forServer(hostIp, port)
              .clean();
   }

}
