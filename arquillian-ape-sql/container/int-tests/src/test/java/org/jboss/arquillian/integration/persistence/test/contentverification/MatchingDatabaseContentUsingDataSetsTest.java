package org.jboss.arquillian.integration.persistence.test.contentverification;

import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.Address;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.persistence.dbunit.api.CustomColumnFilter;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class MatchingDatabaseContentUsingDataSetsTest {

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(UserAccount.class.getPackage())
                // required for remote containers in order to run tests with FEST-Asserts
                .addPackages(true, "org.assertj.core")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
    }

    @PersistenceContext
    EntityManager em;

    @Test
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet(value = "expected-users.yml")
    public void should_verify_database_content_using_custom_data_set() throws Exception {
        // given
        String expectedPassword = "LexLuthor";
        UserAccount user = em.find(UserAccount.class, 2L);

        // when
        user.setPassword("LexLuthor");
        user = em.merge(user);

        // then
        assertThat(user.getPassword()).isEqualTo(expectedPassword);
    }

    @Test
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet(value = "expected-users-with-ids.yml", excludeColumns = "id", orderBy = "id")
    // expected-users-with-ids.yml is constructed in a way that it will fail without the exclusion
    public void should_verify_database_content_using_custom_data_set_with_column_exclusion() throws Exception {
        // given
        String expectedPassword = "LexLuthor";
        UserAccount user = em.find(UserAccount.class, 2L);

        // when
        user.setPassword("LexLuthor");
        user = em.merge(user);

        // then
        assertThat(user.getPassword()).isEqualTo(expectedPassword);
    }

    @Test
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet(value = "users.yml", excludeColumns = {"id", "useraccount.password"})
    public void should_verify_database_content_using_custom_data_set_with_multiple_columns_exclusion() throws Exception {
        // given
        String expectedPassword = "LexLuthor";
        UserAccount user = em.find(UserAccount.class, 2L);

        // when
        user.setPassword("LexLuthor");
        user = em.merge(user);

        // then
        assertThat(user.getPassword()).isEqualTo(expectedPassword);
    }

    @Test
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet(value = "users.yml", excludeColumns = {"useraccount.password"})
    // id excluded in arquillian.xml
    public void should_verify_database_content_using_custom_data_set_with_implicit_multiple_columns_exclusion() throws Exception {
        // given
        String expectedPassword = "LexLuthor";
        UserAccount user = em.find(UserAccount.class, 2L);

        // when
        user.setPassword("LexLuthor");

        // then
        assertThat(user.getPassword()).isEqualTo(expectedPassword);
    }

    @Test
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet(value = {"expected-addresses.yml"}, orderBy = {"address.streetName"}, excludeColumns = {"id"})
    // The order of INSERTs for Addresses is dictated by hashcode values, which differ across test runs.
    // Verify the row order by ordering on the streetName column.
    public void should_verify_database_content_using_custom_data_set_with_custom_row_order() throws Exception {
        // given
        UserAccount user = em.find(UserAccount.class, 1L);
        Address residentialAddress = new Address("Testing Street", 7, "JavaPolis", 1234);
        Address officeAddress = new Address("Main Street", 1, "JavaPolis", 5678);

        // when
        user.addAddress(residentialAddress);
        user.addAddress(officeAddress);
        user = em.merge(user);

        // then
        assertThat(user.getAddresses()).hasSize(2);
    }

    @Test
    @UsingDataSet("simple-with-negative-ids.yml")
    @ShouldMatchDataSet("simple-with-negative-ids.yml")
    public void should_verify_database_content_using_custom_data_set_respecting_ordering_by_proper_type() throws Exception {
    }

    @Test
    @UsingDataSet("three-users.yml")
    @ShouldMatchDataSet(value = {"three-users.yml"}, orderBy = {"id"})
    public void should_verify_database_content_using_custom_data_set_with_order_by_number_type_column() throws Exception {
    }

    @Test
    @UsingDataSet("three-users.yml")
    @ShouldMatchDataSet(value = {"datasets/three-users-with-fake-firstname.yml"}, orderBy = {"id"})
    @CustomColumnFilter(FirstNameColumnFilter.class)
    public void should_filter_non_existing_columns_using_custom_filter() throws Exception {
    }

    @Test
    @UsingDataSet("three-users.yml")
    @ShouldMatchDataSet(value = "datasets/three-users-with-fake-names.yml", orderBy = {"id"})
    @CustomColumnFilter({FirstNameColumnFilter.class, LastNameColumnFilter.class})
    public void should_filter_non_existing_columns_using_custom_filters_combined() throws Exception {
    }

    @Test
    @UsingDataSet("three-users.yml")
    @ShouldMatchDataSet(value = "datasets/three-users-with-fake-names.yml", orderBy = {"id"})
    @CustomColumnFilter(NameColumnFilter.class)
    public void should_filter_non_existing_columns_using_custom_filter_from_external_class_which_should_be_autodeployed() throws Exception {
    }

    public static class FirstNameColumnFilter extends DefaultColumnFilter {
        public FirstNameColumnFilter() {
            excludeColumn("first*");
        }
    }

    public static class LastNameColumnFilter extends DefaultColumnFilter {
        public LastNameColumnFilter() {
            excludeColumn("last*");
        }
    }
}
