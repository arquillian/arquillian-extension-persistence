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
package org.jboss.arquillian.example;

import static org.fest.assertions.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UserPersistenceTest
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
   @Data({"datasets/single-user.xml", "datasets/address.yml"})
   public void shouldHaveAddressLinkedToUserAccountUsingMultipleFiles() throws Exception
   {
      // given
      String expectedCity = "Metropolis";
      UserAccount user = em.find(UserAccount.class, 1L);

      // when
      Address address = user.getAddresses().iterator().next();

      // then
      assertThat(user.getAddresses()).hasSize(1);
      assertThat(address.getCity()).isEqualTo(expectedCity);
   }

   @Test
   @Data("datasets/single-user.xls")
   public void shouldFindUserUsingExcelDatasetAndDataSource() throws Exception
   {
      // given
      String expectedUsername = "doovde";

      // when
      UserAccount user = em.find(UserAccount.class, 1L);

      // then
      assertThat(user.getUsername()).isEqualTo(expectedUsername);
   }

   @Test
   @Data("datasets/single-user.xml")
   public void shouldFindUserUsingXmlDatasetAndDataSource() throws Exception
   {
      // given
      String expectedUsername = "doovde";

      // when
      UserAccount user = em.find(UserAccount.class, 1L);

      // then
      assertThat(user.getUsername()).isEqualTo(expectedUsername);
   }

   @Test
   @Data("datasets/users.yml")
   @Expected("datasets/expected-users.yml")
   public void shouldChangeUserPassword() throws Exception
   {
      // given
      String expectedPassword = "LexLuthor";
      UserAccount user = em.find(UserAccount.class, 2L);

      // when
      user.setPassword("LexLuthor");
      em.merge(user);

      // then
      assertThat(user.getPassword()).isEqualTo(expectedPassword);
   }

   @Test
   @Data("datasets/user-with-address.yml")
   public void shouldHaveAddressLinkedToUserAccount() throws Exception
   {
      // given
      String expectedCity = "Metropolis";
      long userAccountId = 1L;

      // when
      UserAccount user = em.find(UserAccount.class, userAccountId);
      Address address = user.getAddresses().iterator().next();

      // then
      assertThat(user.getAddresses()).hasSize(1);
      assertThat(address.getCity()).isEqualTo(expectedCity);
   }

   @Test
   @Data("datasets/single-user.xml")
   @Expected(value = {"datasets/single-user.xls", "datasets/expected-address.yml"})
   public void shouldAddAddressToUserAccountAndVerifyUsingMultipleFiles() throws Exception
   {
      // given
      UserAccount user = em.find(UserAccount.class, 1L);
      Address address = new Address("Testing Street", 7, "JavaPolis", 1234);

      // when
      user.addAddress(address);
      em.merge(user);

      // then
      assertThat(user.getAddresses()).hasSize(1);
   }

   @Test
   @Transactional
   public void shouldPersistUsersWithinTransaction() throws Exception
   {
      // given
      UserAccount johnSmith = new UserAccount("John", "Smith", "doovde", "password");
      UserAccount clarkKent = new UserAccount("Clark", "Kent", "superman", "LexLuthor");

      // when
      em.persist(johnSmith);
      em.persist(clarkKent);

      // then
      // should be persisted
   }

   @Test
   @Expected("datasets/expected-users.yml")
   public void shouldPersistUsersAndVerifyUsingExpectedMechanism() throws Exception
   {
      // given
      UserAccount johnSmith = new UserAccount("John", "Smith", "doovde", "password");
      UserAccount clarkKent = new UserAccount("Clark", "Kent", "superman", "LexLuthor");

      // when
      em.persist(johnSmith);
      em.persist(clarkKent);

      // then
      // should be persisted - verified by @Expected annotation

   }

}
