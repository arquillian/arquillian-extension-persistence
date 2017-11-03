package org.arquillian.ape.nosql.polyglot;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Map;
import org.arquillian.ape.api.Server;
import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.junit.rule.DeclarativeArquillianPersistenceRule;
import org.arquillian.ape.nosql.mongodb.MongoDb;
import org.arquillian.ape.nosql.redis.Redis;
import org.arquillian.cube.docker.junit.rule.ContainerDslRule;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.assertj.core.api.Assertions.assertThat;

@Server(host = "${arq.cube.docker.redis_3_2_6.ip}", port = "${arq.cube.docker.redis_3_2_6.port.6379:6379}", type = Redis.class)
@Server(host = "${arq.cube.docker.mongo_3_2_12.ip}", port = "${arq.cube.docker.mongo_3_2_12.port.27017:27017}", storage = "test", type = MongoDb.class)
public class PolyglotPersistenceTest {

    @ClassRule
    public static ContainerDslRule redis = new ContainerDslRule("redis:3.2.6")
        .withPortBinding(6379);

    @ClassRule
    public static ContainerDslRule mongo = new ContainerDslRule("mongo:3.2.12")
        .withPortBinding(27017);

    @Rule
    public DeclarativeArquillianPersistenceRule declarativeArquillianPersistenceRule =
        new DeclarativeArquillianPersistenceRule();

    @Test
    @UsingDataSet(value = "booksRedis.json", type = Redis.class)
    @UsingDataSet(value = "booksMongo.json", type = MongoDb.class)
    public void should_populate_redis() {

        int redisPort = Integer.parseInt(System.getProperty("arq.cube.docker.redis_3_2_6.port.6379"));
        final String redisHost = System.getProperty("arq.cube.docker.redis_3_2_6.ip");

        Jedis jedis = new Jedis(redisHost, redisPort);
        final Map<String, String> fieldsOfTheHobbitBook = jedis.hgetAll("The Hobbit");

        assertThat(fieldsOfTheHobbitBook)
            .containsEntry("title", "The Hobbit")
            .containsEntry("numberOfPages", "293");

        int mongoPort = Integer.parseInt(System.getProperty("arq.cube.docker.mongo_3_2_12.port.27017"));
        String mongoHost = System.getProperty("arq.cube.docker.mongo_3_2_12.ip");

        MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
        final MongoDatabase database = mongoClient.getDatabase("test");
        final MongoCollection<Document> book = database.getCollection("Book");
        final FindIterable<Document> documents = book.find();

        assertThat(documents.first())
            .containsEntry("title", "The Lord Of The Rings")
            .containsEntry("numberOfPages", 1184);
    }
}
