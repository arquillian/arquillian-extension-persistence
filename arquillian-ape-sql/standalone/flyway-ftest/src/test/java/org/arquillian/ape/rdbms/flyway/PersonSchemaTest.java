package org.arquillian.ape.rdbms.flyway;

import java.net.URI;
import org.arquillian.ape.rdbms.core.RdbmsPopulator;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.h2.Driver;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.db.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class PersonSchemaTest {


    @Flyway
    @ArquillianResource
    RdbmsPopulator rdbmsPopulator;


    @Test
    public void should_create_person_table() {
        rdbmsPopulator.forUri(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"))
            .withDriver(Driver.class)
            .withUsername("sa")
            .withPassword("")
            .usingDataSet("db/migration")
            .execute();

        Table table = new Table(new Source("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", ""), "person");
        assertThat(table).hasNumberOfColumns(4);
        assertThat(table).column(0).hasColumnName("id");
        assertThat(table).column(1).hasColumnName("name");
        assertThat(table).column(2).hasColumnName("last_name");
        assertThat(table).column(3).hasColumnName("age");
    }

}
