package org.arquilian.ape.nosql.mongodb;

import com.github.fakemongo.Fongo;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.arquillian.ape.nosql.NoSqlPopulator;
import org.arquillian.ape.nosql.mongodb.MongoDb;
import org.bson.Document;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class FongoDbTest {

    @ArquillianResource
    @MongoDb
    NoSqlPopulator populator;

    private static Fongo fongo = new Fongo("db");

    @Test
    public void should_populate_fongodb() {

        populator.project()
            .withStorage("test")
            .usingDataSet("books.json")
            .execute(fongo.getMongo());

        final MongoDatabase database = fongo.getMongo().getDatabase("test");
        final MongoCollection<Document> book = database.getCollection("Book");
        final FindIterable<Document> documents = book.find();

        assertThat(documents.first())
            .containsEntry("title", "The Hobbit")
            .containsEntry("numberOfPages", 293);

    }

}
