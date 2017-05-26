package org.arquillian.integration.ape.dsl;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.arquillian.ape.rdbms.PersistenceTest;
import org.arquillian.ape.rdbms.core.RdbmsPopulator;
import org.arquillian.ape.rdbms.core.configuration.PersistenceConfiguration;
import org.arquillian.ape.rdbms.dbunit.DbUnit;
import org.arquillian.ape.rdbms.dbunit.DbUnitOptions;
import org.arquillian.integration.ape.example.UserAccount;
import org.arquillian.integration.ape.example.deployments.UserPersistenceWarDeploymentTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@PersistenceTest
public class ApeDslIncontainerTest {

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return UserPersistenceWarDeploymentTest.createDeploymentPackage()
            .addAsResource("datasets/single-user.xls")
            .addAsResource("datasets/single-user.xml")
            .addAsResource("datasets/single-user.yml");
    }

    @ArquillianResource
    private PersistenceConfiguration persistenceConfiguration;

    @PersistenceContext
    private EntityManager em;

    @ArquillianResource @DbUnit
    private RdbmsPopulator db;

    @Test
    public void should_find_user_using_excel_dataset_and_data_source() throws Exception {
        // given
        db.forUri(persistenceConfiguration.getDefaultDataSource())
            .usingDataSet("datasets/single-user.xls")
            .execute();
        String expectedUsername = "doovde";

        // when
        UserAccount user = em.find(UserAccount.class, 1L);

        // then
        assertThat(user.getUsername()).isEqualTo(expectedUsername);
    }

    @Test
    public void should_have_timestamp_populated() throws Exception {
        // given
        db.forUri(persistenceConfiguration.getDefaultDataSource())
            .usingDataSet("datasets/single-user.yml")
            .execute();
        final Date expectedOpenDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse("2001-01-01 00:00:00");

        // when
        UserAccount user = em.find(UserAccount.class, 1L);

        // then
        assertThat(user.getOpenDate()).isEqualTo(expectedOpenDate);
    }

    @Test
    public void should_find_user_using_xml_dataset() throws Exception {
        // given
        db.forUri(persistenceConfiguration.getDefaultDataSource())
            .usingDataSet("datasets/single-user.xml")
            .execute();
        String expectedUsername = "doovde";

        // when
        UserAccount user = em.find(UserAccount.class, 1L);

        // then
        assertThat(user.getUsername()).isEqualTo(expectedUsername);
    }

    @Test
    public void should_find_user_using_yaml_dataset() throws Exception {
        // given
        db.forUri(persistenceConfiguration.getDefaultDataSource())
            .usingDataSet("datasets/single-user.yml")
            .withOptions(DbUnitOptions.options().caseSensitiveTableNames(true).build())
            .execute();
        String expectedUsername = "doovde";

        // when
        UserAccount user = em.find(UserAccount.class, 1L);

        // then
        assertThat(user.getUsername()).isEqualTo(expectedUsername);
        assertThat(user.getNickname()).isNull();
    }
}
