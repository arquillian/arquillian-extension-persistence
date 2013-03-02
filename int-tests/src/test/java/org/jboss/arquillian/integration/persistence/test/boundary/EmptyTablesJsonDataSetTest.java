package org.jboss.arquillian.integration.persistence.test.boundary;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.integration.persistence.util.UserPersistenceAssertion;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE)
public class EmptyTablesJsonDataSetTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
                       .addPackage(UserAccount.class.getPackage())
                       .addClasses(Query.class, UserPersistenceAssertion.class)
                       // required for remote containers in order to run tests with FEST-Asserts
                       .addPackages(true, "org.fest")
                       .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
   }

   @PersistenceContext
   EntityManager em;

   @Test @InSequence(1)
   @UsingDataSet("users.json")
   public void should_fail_when_empty_set_yaml_used_for_verifying_content_of_non_empty_database()
   {
      new UserPersistenceAssertion(em).assertUserAccountsStored();
   }

   @Test @InSequence(2)
   @UsingDataSet("empty/empty-tables.json")
   public void should_clean_when_yaml_with_empty_tables_provided() throws Exception
   {
      new UserPersistenceAssertion(em).assertNoUserAccountsStored();
   }

}
