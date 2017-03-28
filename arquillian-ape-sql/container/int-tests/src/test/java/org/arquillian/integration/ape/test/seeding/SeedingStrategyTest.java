package org.arquillian.integration.ape.test.seeding;

import org.arquillian.integration.ape.example.UserAccount;
import org.arquillian.integration.ape.util.Query;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.arquillian.ape.rdbms.DataSeedStrategy;
import org.arquillian.ape.rdbms.SeedDataUsing;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class SeedingStrategyTest {

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(UserAccount.class.getPackage())
                .addClass(Query.class)
                .addPackages(true, "org.assertj.core")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource("test-persistence.xml", "persistence.xml");
    }

    @PersistenceContext
    EntityManager em;

    @Test
    @ApplyScriptBefore("lex-luthor.sql")
    @UsingDataSet("two-users.yml")
    public void should_insert_new_rows_next_to_already_existing_ones() throws Exception {
        // given
        int expectedAmountOfUserEntries = 3;

        // when
        @SuppressWarnings("unchecked")
        List<UserAccount> userAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();

        // then
        assertThat(userAccounts).hasSize(expectedAmountOfUserEntries);
    }

    @Test
    @ApplyScriptBefore("lex-luthor.sql")
    @UsingDataSet("two-users.yml")
    @SeedDataUsing(DataSeedStrategy.CLEAN_INSERT)
    public void should_insert_new_rows_and_remove_already_existing_ones() throws Exception {
        // given
        int expectedAmountOfUserEntries = 2;

        // when
        @SuppressWarnings("unchecked")
        List<UserAccount> userAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();

        // then
        assertThat(userAccounts).hasSize(expectedAmountOfUserEntries);
    }

    @Test
    @ApplyScriptBefore("lex-luthor.sql")
    @UsingDataSet("updated-lex-luthor.yml")
    @SeedDataUsing(DataSeedStrategy.UPDATE)
    public void should_insert_update_already_existing_ones() throws Exception {
        // given
        UserAccount updatedLex = new UserAccount("lex", "luthor", "lexlex", "updated");

        // when
        UserAccount lexLuthor = em.find(UserAccount.class, 3L);

        assertUserAccountsAreEqual(lexLuthor, updatedLex);
    }

    @Test
    @ApplyScriptBefore("lex-luthor.sql")
    @UsingDataSet({"two-users.yml", "updated-lex-luthor.yml"})
    @SeedDataUsing(DataSeedStrategy.REFRESH)
    public void should_insert_new_rows_and_update_already_existing_ones() throws Exception {
        // given
        UserAccount updatedLex = new UserAccount("lex", "luthor", "lexlex", "updated");

        // when
        @SuppressWarnings("unchecked")
        List<UserAccount> userAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();
        UserAccount lexLuthor = em.find(UserAccount.class, 3L);

        // then
        assertUserAccountsAreEqual(lexLuthor, updatedLex);
        assertThat(userAccounts).hasSize(3);
    }

    // -- Test utility methods

    private static void assertUserAccountsAreEqual(UserAccount actual, UserAccount expected) {
        assertThat(actual.getFirstname()).isEqualTo(expected.getFirstname());
        assertThat(actual.getLastname()).isEqualTo(expected.getLastname());
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
    }

}
