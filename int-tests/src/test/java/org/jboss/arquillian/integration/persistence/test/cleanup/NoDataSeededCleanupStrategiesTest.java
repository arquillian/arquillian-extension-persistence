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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * All tests are wrapped in transaction.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@RunWith(Arquillian.class)
@CreateSchema("schema/create.sql")
public class NoDataSeededCleanupStrategiesTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war").addPackage(UserAccount.class.getPackage())
                        .addClass(Query.class)
                        // required for remote containers in order to run tests with FEST-Asserts
                        .addPackages(true, "org.assertj.core")
                        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                        .addAsResource("test-persistence-no-generate.xml", "META-INF/persistence.xml");
   }

   @PersistenceContext
   EntityManager em;

   @Test
   @Cleanup(phase =  TestExecutionPhase.BEFORE)
   public void should_not_have_any_user_in_database_default_strategy() throws Exception
   {
      assertNoUserAccountsStored();
   }

   @Test
   @Cleanup(strategy =  CleanupStrategy.USED_TABLES_ONLY)
   public void should_not_have_any_user_in_database_used_tables_only_strategy() throws Exception
   {
      assertNoUserAccountsStored();
   }

   @Test
   @Cleanup(strategy =  CleanupStrategy.USED_ROWS_ONLY)
   public void should_not_have_any_user_in_database_used_rows_only_strategy() throws Exception
   {
      assertNoUserAccountsStored();
   }

   // Private helper methods

   private void assertNoUserAccountsStored()
   {
      @SuppressWarnings("unchecked")
      List<UserAccount> savedUserAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();
      assertThat(savedUserAccounts).isEmpty();
   }

}
