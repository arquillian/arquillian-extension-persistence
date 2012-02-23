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
package org.jboss.arquillian.persistence.test;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.persistence.UsingScript;
import org.jboss.arquillian.persistence.test.Address;
import org.jboss.arquillian.persistence.test.UserAccount;
import org.jboss.arquillian.persistence.test.util.Query;
import org.junit.Test;

/**
 * Defines tests to perform with Persistence Extension but leaves deployment declaration as
 * responsibility of the concrete class.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public abstract class NonDeployableUserPersistenceTest
{

   @PersistenceContext
   EntityManager em;

   @Test
   @UsingDataSet("datasets/single-user.xls")
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
   @UsingDataSet({"single-user.xml", "address.yml"}) // Convention over configuration - no need to specify 'datasets' folder
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
   @UsingDataSet("single-user.xml")
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
   @UsingDataSet("users.yml")
   @ShouldMatchDataSet("expected-users.yml")
   public void shouldChangePassword() throws Exception
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
   @UsingScript("users.sql")
   @ShouldMatchDataSet("expected-users.yml")
   public void shouldChangePasswordUsingSqlToSeedData() throws Exception
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
   @UsingDataSet("users.json")
   @ShouldMatchDataSet("expected-users.json")
   public void shouldChangeUserPasswordUsingJsonDataSets() throws Exception
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
   @UsingDataSet("user-with-address.yml")
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
   @UsingDataSet("single-user.xml")
   @ShouldMatchDataSet({"single-user.xls", "datasets/expected-address.yml"})
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
      em.flush();
      em.clear();

      // then
      @SuppressWarnings("unchecked")
      List<UserAccount> savedUserAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();
      assertThat(savedUserAccounts).hasSize(2);
   }

   @Test
   @Transactional(TransactionMode.ROLLBACK)
   public void shouldPersistUsersAndRollbackTransactionAfterTestExecution() throws Exception
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
      @SuppressWarnings("unchecked")
      List<UserAccount> savedUserAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();
      assertThat(savedUserAccounts).hasSize(2);
   }

   @Test
   @ShouldMatchDataSet("expected-users.yml")
   public void shouldPersistUsersAndVerifyUsingMatchingMechanism() throws Exception
   {
      // given
      UserAccount johnSmith = new UserAccount("John", "Smith", "doovde", "password");
      UserAccount clarkKent = new UserAccount("Clark", "Kent", "superman", "LexLuthor");

      // when
      em.persist(johnSmith);
      em.persist(clarkKent);

      // then
      // should be persisted - verified by @ShouldMatchDataSet annotation
   }

   @Test
   @UsingDataSet("users.xml")
   // This test will fail if column sensing for FlatXmlDataSet is not enabled
   // See http://www.dbunit.org/faq.html#differentcolumnnumber
   public void shouldFindTwoUsersUsingFlatXmlDataSet() throws Exception
   {
      // given
      int expectedUserAmount = 2;
      String expectedNicknameOfSecondUser = "superman";

      // when
      @SuppressWarnings("unchecked")
      List<UserAccount> userAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();

      // then
      assertThat(userAccounts).hasSize(expectedUserAmount);
      assertThat(userAccounts.get(1).getNickname()).isEqualTo(expectedNicknameOfSecondUser);
   }

   @Test
   @UsingScript("clark-kent-with-nickname.sql")
   @ShouldMatchDataSet("clark-kent-without-nickname.xml")
   // This test will fail if replaceable set is not used
   // See http://www.dbunit.org/apidocs/org/dbunit/dataset/ReplacementDataSet.html
   public void shouldCompareNullValueDefinedInFlatXmlDataSet() throws Exception
   {
      // given
      UserAccount clarkKent = em.find(UserAccount.class, 1L);

      // when
      clarkKent.setNickname(null);
      em.merge(clarkKent);

      // then
      // verified by DataSet comparision
   }

   @Test
   @UsingScript("clark-kent-with-nickname.sql")
   @ShouldMatchDataSet("clark-kent-without-nickname.yml")
   public void shouldCompareNullValueDefinedInYamlDataSet() throws Exception
   {
      // given
      UserAccount clarkKent = em.find(UserAccount.class, 1L);

      // when
      clarkKent.setNickname(null);
      em.merge(clarkKent);

      // then
      // verified by DataSet comparision
   }

   @Test
   @UsingScript("clark-kent-with-nickname.sql")
   @ShouldMatchDataSet("clark-kent-without-nickname.json")
   public void shouldCompareNullValueDefinedInJsonDataSet() throws Exception
   {
      // given
      UserAccount clarkKent = em.find(UserAccount.class, 1L);

      // when
      clarkKent.setNickname(null);
      em.merge(clarkKent);

      // then
      // verified by DataSet comparision
   }

}
