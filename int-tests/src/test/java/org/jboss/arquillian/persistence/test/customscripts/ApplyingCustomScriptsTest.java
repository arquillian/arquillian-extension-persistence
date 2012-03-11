package org.jboss.arquillian.persistence.test.customscripts;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.test.usecase.UserAccount;
import org.jboss.arquillian.persistence.testextension.event.annotation.ExecuteScriptsShouldBeTriggered;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ApplyingCustomScriptsTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
                       .addPackage(UserAccount.class.getPackage())
                       .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsManifestResource("test-persistence.xml", "persistence.xml");
   }

   @PersistenceContext
   EntityManager em;

   @Test
   @Cleanup(phase = TestExecutionPhase.NONE)
   @ApplyScriptAfter("users.sql")
   @ShouldMatchDataSet("two-users.yml")
   @ExecuteScriptsShouldBeTriggered(TestExecutionPhase.AFTER)
   public void should_add_users_after_test_using_custom_script() throws Exception
   {
   }

   @Test
   @ApplyScriptAfter("clark-kent.sql")
   @ShouldMatchDataSet("two-users.yml")
   @ExecuteScriptsShouldBeTriggered(TestExecutionPhase.AFTER)
   public void should_add_users_after_test_to_already_created_entries_using_custom_script() throws Exception
   {
      // given
      UserAccount johnSmith = new UserAccount("John", "Smith", "doovde", "password");

      // when
      em.persist(johnSmith);

      // then
      // superman should be added after test execution
      // and data should be compared using dataset defined in @ShouldMatchDataSet
   }

}
