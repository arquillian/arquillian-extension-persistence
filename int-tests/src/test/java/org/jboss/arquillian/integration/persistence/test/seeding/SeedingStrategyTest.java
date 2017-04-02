package org.jboss.arquillian.integration.persistence.test.seeding;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.DataSeedStrategy;
import org.jboss.arquillian.persistence.SeedDataUsing;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class SeedingStrategyTest {

    @PersistenceContext
    EntityManager em;

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addPackage(UserAccount.class.getPackage())
            .addClass(Query.class)
            .addPackages(true, "org.assertj.core")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("test-persistence.xml", "persistence.xml");
    }

    private static void assertUserAccountsAreEqual(UserAccount actual, UserAccount expected) {
        assertThat(actual.getFirstname()).isEqualTo(expected.getFirstname());
        assertThat(actual.getLastname()).isEqualTo(expected.getLastname());
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
    }

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

    // -- Test utility methods

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
}
