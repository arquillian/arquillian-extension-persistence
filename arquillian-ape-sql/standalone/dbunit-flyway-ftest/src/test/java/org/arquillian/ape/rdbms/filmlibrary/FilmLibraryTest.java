package org.arquillian.ape.rdbms.filmlibrary;

import java.net.URI;
import org.arquillian.ape.junit.rule.ArquillianPersistenceRule;
import org.arquillian.ape.rdbms.core.RdbmsPopulator;
import org.arquillian.ape.rdbms.dbunit.DbUnit;
import org.arquillian.ape.rdbms.flyway.Flyway;
import org.arquillian.cube.docker.impl.client.containerobject.dsl.AwaitBuilder;
import org.arquillian.cube.docker.junit.rule.ContainerDslRule;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.postgresql.Driver;

import static org.arquillian.ape.rdbms.dbunit.DbUnitOptions.options;
import static org.assertj.db.api.Assertions.assertThat;

public class FilmLibraryTest {

    public static final String DB = "filmlibrary";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "postgres";

    @ClassRule
    public static ContainerDslRule postgresql = new ContainerDslRule("postgres:9.6.2-alpine")
        .withPortBinding("15432->5432")
        .withEnvironment("POSTGRES_PASSWORD", PASSWORD,
            "POSTGRES_USER", USERNAME,
            "POSTGRES_DB", DB)
        .withAwaitStrategy(AwaitBuilder.logAwait("LOG:  autovacuum launcher started", 2));

    @Rule
    public ArquillianPersistenceRule arquillianPersistenceRule = new ArquillianPersistenceRule();

    @Flyway
    @ArquillianResource
    RdbmsPopulator flywayRdbmsPopulator;

    @DbUnit
    @ArquillianResource
    RdbmsPopulator dbUnitRdbmsPopulator;

    @Test
    public void should_find_all_hollywood_films() {

        final URI jdbcUri = URI.create(
            String.format("jdbc:postgresql://%s:%d/%s", postgresql.getIpAddress(), postgresql.getBindPort(5432), DB));

        flywayRdbmsPopulator
            .forUri(jdbcUri)
            .withDriver(Driver.class)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .usingDataSet("db/migration")
            .execute();

        dbUnitRdbmsPopulator.forUri(jdbcUri)
            .withDriver(Driver.class)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .usingDataSet("hollywoodfilms.yml")
            .withOptions(options()
                            .schema("hollywood")
                            .build()
            )
            .execute();

        final Table table = new Table(new Source(jdbcUri.toString() + "?currentSchema=hollywood", USERNAME, PASSWORD), "films");
        assertThat(table).column("title")
            .value().isEqualTo("Trolls");

        flywayRdbmsPopulator
            .forUri(jdbcUri)
            .withDriver(Driver.class)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .usingDataSet("db/migration")
            .clean();

    }

}
