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
package org.jboss.arquillian.integration.persistence.test.boundary;

import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class UsedRowsOnlyCleanupStrategyTest extends Arquillian
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
                       .addPackage(UserAccount.class.getPackage())
                       .addClass(Query.class)
                       // required for remote containers in order to run tests with FEST-Asserts
                       .addPackages(true, "org.fest")
                       .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsManifestResource("test-persistence.xml", "persistence.xml");
   }

   @PersistenceContext
   EntityManager em;

   @Test
   @ApplyScriptBefore({ "clark-kent-with-nickname.sql", "john-smith.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void insert_data_for_test()
   {
   }

   @Test
   @UsingDataSet("users.yml") // both users are without nicknames
   @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.USED_ROWS_ONLY)
   public void should_cleanup_database_content_matching_users_from_dataset()
   {
      UserAccount clarkKent = em.find(UserAccount.class, 2L);
      assertThat(clarkKent.getNickname()).isNullOrEmpty();
   }

}