package org.jboss.arquillian.persistence.test.seeding;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
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
public class SeedingDatabaseByApplyingCustomScriptTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
                       .addPackage(UserAccount.class.getPackage())
                       .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsManifestResource("test-persistence.xml", "persistence.xml");
   }

   @Test
   @ApplyScriptBefore("users.sql")
   @ShouldMatchDataSet("two-users.yml")
   @ExecuteScriptsShouldBeTriggered(TestExecutionPhase.BEFORE)
   public void should_seed_database_using_custom_script() throws Exception
   {
   }
}
