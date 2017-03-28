package org.jboss.arquillian.populator.sql;

import org.assertj.db.api.Assertions;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.h2.Driver;
import org.h2.tools.RunScript;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.populator.nosql.api.SqlPopulator;
import org.jboss.arquillian.populator.sql.core.Sql;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.nio.charset.Charset;
import java.sql.SQLException;

import static org.assertj.db.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class PersonTest {


    @Sql
    @ArquillianResource
    SqlPopulator sqlPopulator;

    @BeforeClass
    public static void createSchema() throws SQLException {
        // H2 tool for creating schema

        RunScript.execute("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                "sa", "", "src/test/resources/schema.sql", Charset.forName("UTF-8"), false);
    }

    @Test
    public void should_find_all_persons() {
        sqlPopulator.forUri(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"))
                        .withDriver(Driver.class)
                        .withUsername("sa")
                        .withPassword("")
                        .usingDataSet("person.xml")
                    .execute();


        Table table = new Table(new Source("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", ""), "PERSON");
        assertThat(table).column("NAME")
                         .value().isEqualTo("Bob")
                         .value().isEqualTo("Alice")
                         .value().isEqualTo("Charlie");
    }

}
