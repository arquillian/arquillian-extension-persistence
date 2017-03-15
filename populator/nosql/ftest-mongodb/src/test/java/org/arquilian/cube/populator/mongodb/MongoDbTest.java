package org.arquilian.cube.populator.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.HostPort;
import org.bson.Document;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.populator.nosql.api.NoSqlPopulator;
import org.jboss.arquillian.populator.nosql.mongodb.MongoDb;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class MongoDbTest {

   @HostIp
   private String hostIp;

   @HostPort(containerName = "mongodb", value = 27017)
   int port;

   @ArquillianResource
   @MongoDb
   NoSqlPopulator populator;

   @Test
   public void should_populate_mongodb() {
      populator.forServer(hostIp, port)
              .withDatabase("test")
              .usingDataSet("/books.json")
              .execute();

      MongoClient mongoClient = new MongoClient(hostIp, port);
      final MongoDatabase database = mongoClient.getDatabase("test");
      final MongoCollection<Document> book = database.getCollection("Book");
      final FindIterable<Document> documents = book.find();

      assertThat(documents.first())
              .containsEntry("title", "The Hobbit")
              .containsEntry("numberOfPages", 293);

   }

   @After
   public void cleanDatabase() {
      populator.forServer(hostIp, port)
              .withDatabase("test")
              .clean();
   }

}
