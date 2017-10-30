package org.arquillian.ape.rdbms.dbunit.junit;

import java.net.URI;
import java.nio.charset.Charset;
import java.sql.SQLException;
import org.arquillian.ape.junit.rule.ArquillianPersistenceRule;
import org.arquillian.ape.rdbms.core.RdbmsPopulator;
import org.arquillian.ape.rdbms.dbunit.DbUnit;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.h2.Driver;
import org.h2.tools.RunScript;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.db.api.Assertions.assertThat;

public class PersonTest {

    @Rule
    public ArquillianPersistenceRule arquillianPersistenceRule = new ArquillianPersistenceRule();

    @DbUnit
    @ArquillianResource
    RdbmsPopulator rdbmsPopulator;

    @BeforeClass
    public static void createSchema() throws SQLException {
        // H2 tool for creating schema

        RunScript.execute("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            "sa", "", "src/test/resources/schema.sql", Charset.forName("UTF-8"), false);
    }

    @Test
    public void should_find_all_heroes() {
        rdbmsPopulator.forUri(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"))
            .withDriver(Driver.class)
            .withUsername("sa")
            .withPassword("")
            .usingDataSet("heroes.yml")
            .execute();

        Table table = new Table(new Source("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", ""), "person");
        assertThat(table).column("name")
            .value().isEqualTo("Clark")
            .value().isEqualTo("Lex");

        rdbmsPopulator.forUri(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"))
            .withDriver(Driver.class)
            .withUsername("sa")
            .withPassword("")
            .usingDataSet("heroes.yml")
            .clean();
    }

}