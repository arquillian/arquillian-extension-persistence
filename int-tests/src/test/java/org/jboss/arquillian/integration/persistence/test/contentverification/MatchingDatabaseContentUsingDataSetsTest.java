package org.jboss.arquillian.integration.persistence.test.contentverification;

import static org.fest.assertions.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
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
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MatchingDatabaseContentUsingDataSetsTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
                       .addPackage(UserAccount.class.getPackage())
                       // required for remote containers in order to run tests with FEST-Asserts
                       .addPackages(true, "org.fest")
                       .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsManifestResource("test-persistence.xml", "persistence.xml");
   }

   @PersistenceContext
   EntityManager em;

   @Test
   @UsingDataSet("users.yml")
   @ShouldMatchDataSet(value = "expected-users.yml", excludeColumns = "id")
   public void should_verify_database_content_using_custom_data_set() throws Exception
   {
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
   @ShouldMatchDataSet(value = "expected-users-with-ids.yml", excludeColumns = "id")
   // expected-users-with-ids.yml is constructed in a way that it will fail withouth exclusion
   public void should_verify_database_content_using_custom_data_set_with_column_exclusion() throws Exception
   {
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
   @ShouldMatchDataSet(value = "users.yml", excludeColumns = { "id", "useraccount.password" })
   public void should_verify_database_content_using_custom_data_set_with_multiple_columns_exclusion() throws Exception
   {
      // given
      String expectedPassword = "LexLuthor";
      UserAccount user = em.find(UserAccount.class, 2L);

      // when
      user.setPassword("LexLuthor");
      user = em.merge(user);

      // then
      assertThat(user.getPassword()).isEqualTo(expectedPassword);
   }

}
