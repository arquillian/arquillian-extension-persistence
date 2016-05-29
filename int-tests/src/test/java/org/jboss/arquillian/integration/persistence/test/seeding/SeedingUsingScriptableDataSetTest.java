package org.jboss.arquillian.integration.persistence.test.seeding;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class SeedingUsingScriptableDataSetTest {

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(UserAccount.class.getPackage())
                .addClass(Query.class)
                .addPackages(true, "org.assertj.core")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
        return war;
    }

    @PersistenceContext
    EntityManager em;

    @Test
    @UsingDataSet("expected-users-scriptable.yml")
    @ShouldMatchDataSet(value = "expected-users-scriptable.yml",excludeColumns = "id")
    public void should_seed_dataset_using_groovy_and_javascript() throws Exception {
        // given
        int expectedAmountOfUserEntries = 1;

        // when
        @SuppressWarnings("unchecked")
        List<UserAccount> userAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();

        // then
        assertThat(userAccounts).hasSize(expectedAmountOfUserEntries);

        UserAccount userAccount = userAccounts.get(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = sdf.parse(sdf.format(new Date()));

        assertThat(userAccount.getUsername()).isEqualTo("superman");
        assertThat(userAccount.getOpenDate()).isEqualTo(now);

    }

}
