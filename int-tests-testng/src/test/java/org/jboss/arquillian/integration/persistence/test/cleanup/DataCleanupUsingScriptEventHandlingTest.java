/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.integration.persistence.test.cleanup;

import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.testng.annotations.Test;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.testextension.event.annotation.CleanupShouldNotBeTriggered;
import org.jboss.arquillian.integration.persistence.testextension.event.annotation.CleanupUsingScriptShouldBeTriggered;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@Transactional
public class DataCleanupUsingScriptEventHandlingTest extends Arquillian
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
                       .addPackage(UserAccount.class.getPackage())
                       // required for remote containers in order to run tests with FEST-Asserts
                       .addPackages(true, "org.fest")
                       .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
   }

   @PersistenceContext
   EntityManager em;

   @Test
   @CleanupUsingScript("delete-users.sql")
   @CleanupShouldNotBeTriggered
   @CleanupUsingScriptShouldBeTriggered(TestExecutionPhase.AFTER)
   public void should_cleanup_data_using_custom_sql_script_after_test_when_not_specified() throws Exception
   {
      // given
      UserAccount johnSmith = new UserAccount("John", "Smith", "doovde", "password");
      UserAccount clarkKent = new UserAccount("Clark", "Kent", "superman", "LexLuthor");

      // when
      em.persist(johnSmith);
      em.persist(clarkKent);
      em.flush();
      em.clear();

      // then
      // data cleanup should be called before the test
   }

   @Test
   @CleanupUsingScript(value = "delete-users.sql", phase = TestExecutionPhase.BEFORE)
   @ShouldMatchDataSet("empty.xml")
   @CleanupShouldNotBeTriggered
   @CleanupUsingScriptShouldBeTriggered(TestExecutionPhase.BEFORE)
   public void should_cleanup_data_using_custom_sql_script_after_test() throws Exception
   {
      // given
      UserAccount johnSmith = new UserAccount("John", "Smith", "doovde", "password");
      UserAccount clarkKent = new UserAccount("Clark", "Kent", "superman", "LexLuthor");

      // when
      em.persist(johnSmith);
      em.persist(clarkKent);
      em.flush();
      em.clear();

      // then
      // data cleanup should be called after the test
   }

}
