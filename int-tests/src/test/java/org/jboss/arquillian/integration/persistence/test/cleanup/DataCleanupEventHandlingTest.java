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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.testextension.event.annotation.CleanupShouldBeTriggered;
import org.jboss.arquillian.integration.persistence.testextension.event.annotation.CleanupShouldNotBeTriggered;
import org.jboss.arquillian.integration.persistence.testextension.event.annotation.CleanupUsingScriptShouldNotBeTriggered;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@RunWith(Arquillian.class)
@Transactional
public class DataCleanupEventHandlingTest
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
   @CleanupShouldBeTriggered(TestExecutionPhase.BEFORE)
   @CleanupUsingScriptShouldNotBeTriggered
   public void should_cleanup_data_before_test_when_not_specified() throws Exception
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
   @Cleanup(phase = TestExecutionPhase.AFTER)
   @CleanupShouldBeTriggered(TestExecutionPhase.AFTER)
   @CleanupUsingScriptShouldNotBeTriggered
   public void should_cleanup_data_after_test() throws Exception
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

   @Test
   @Cleanup(phase = TestExecutionPhase.NONE)
   @CleanupShouldNotBeTriggered
   public void should_not_cleanup_data() throws Exception
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
      // data clean up should not be performed
   }

}
