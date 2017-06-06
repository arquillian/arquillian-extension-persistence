package org.arquillian.rdbms.flyway;

import org.arquillian.ape.api.Authentication;
import org.arquillian.ape.api.Uri;
import org.arquillian.ape.api.UsingDataSet;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.h2.Driver;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.db.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@org.arquillian.ape.api.Driver(Driver.class)
@Authentication(username = "sa", password = "")
@Uri("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
public class PersonSchemaTest {

    @Test
    @UsingDataSet("db/migration")
    public void should_create_person_table() {
        Table table = new Table(new Source("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", ""), "person");
        assertThat(table).hasNumberOfColumns(4);
        assertThat(table).column(0).hasColumnName("id");
        assertThat(table).column(1).hasColumnName("name");
        assertThat(table).column(2).hasColumnName("last_name");
        assertThat(table).column(3).hasColumnName("age");
    }

}
