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
package org.arquillian.integration.ape.example;

import org.arquillian.integration.ape.util.Query;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.arquillian.ape.rdbms.CleanupUsingScript;
import org.arquillian.ape.rdbms.ShouldMatchDataSet;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Defines tests to perform with Persistence Extension but leaves deployment declaration as
 * responsibility of the concrete class.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public abstract class NonDeployableUserPersistenceTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    @UsingDataSet("datasets/single-user.xls")
    public void should_find_user_using_excel_dataset_and_data_source() throws Exception {
        // given
        String expectedUsername = "doovde";

        // when
        UserAccount user = em.find(UserAccount.class, 1L);

        // then
        assertThat(user.getUsername()).isEqualTo(expectedUsername);
    }

    @Test
    @UsingDataSet("datasets/single-user.yml")
    public void should_have_timestamp_populated() throws Exception {
        // given
        final Date expectedOpenDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse("2001-01-01 00:00:00");

        // when
        UserAccount user = em.find(UserAccount.class, 1L);

        // then
        assertThat(user.getOpenDate()).isEqualTo(expectedOpenDate);
    }

    @Test
    @UsingDataSet({"single-user.xml", "address.yml"})
    // Convention over configuration - no need to specify 'datasets' folder
    public void should_have_address_linked_to_user_account_using_multiple_files() throws Exception {
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
    public void should_find_user_using_xml_dataset() throws Exception {
        // given
        String expectedUsername = "doovde";

        // when
        UserAccount user = em.find(UserAccount.class, 1L);

        // then
        assertThat(user.getUsername()).isEqualTo(expectedUsername);
    }

    @Test
    @UsingDataSet("single-user.yml")
    public void should_find_user_using_yaml_dataset() throws Exception {
        // given
        String expectedUsername = "doovde";

        // when
        UserAccount user = em.find(UserAccount.class, 1L);

        // then
        assertThat(user.getUsername()).isEqualTo(expectedUsername);
        assertThat(user.getNickname()).isNull();
    }

    @Test
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet("expected-users.yml")
    public void should_change_password() throws Exception {
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
    @ApplyScriptBefore("users.sql")
    @ShouldMatchDataSet("expected-users.yml")
    public void should_change_password_using_sql_to_seed_data() throws Exception {
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
    @UsingDataSet("users.json")
    @ShouldMatchDataSet("expected-users.json")
    public void should_change_user_password_using_json_data_sets() throws Exception {
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
    @UsingDataSet("user-with-address.yml")
    public void should_have_address_linked_to_user_account() throws Exception {
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
    public void should_add_address_to_user_account_and_verify_using_multiple_files() throws Exception {
        // given
        UserAccount user = em.find(UserAccount.class, 1L);
        Address address = new Address("Testing Street", 7, "JavaPolis", 1234);

        // when
        user.addAddress(address);
        user = em.merge(user);

        // then
        assertThat(user.getAddresses()).hasSize(1);
    }

    @Test
    @Transactional
    public void should_persist_users_within_transaction() throws Exception {
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
    public void should_persist_users_and_rollback_transaction_after_test_execution() throws Exception {
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
    public void should_persist_users_and_verify_using_matching_mechanism() throws Exception {
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
    public void should_find_two_users_using_flat_xml_data_set() throws Exception {
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
    @ApplyScriptBefore("clark-kent-with-nickname.sql")
    @ShouldMatchDataSet("clark-kent-without-nickname.xml")
    // This test will fail if replaceable set is not used
    // See http://www.dbunit.org/apidocs/org/dbunit/dataset/ReplacementDataSet.html
    public void should_compare_null_value_defined_in_flat_xml_data_set() throws Exception {
        // given
        UserAccount clarkKent = em.find(UserAccount.class, 2L);

        // when
        clarkKent.setNickname(null);
        clarkKent = em.merge(clarkKent);

        // then
        // verified by DataSet comparison
    }

    @Test
    @ApplyScriptBefore("clark-kent-with-nickname.sql")
    @ShouldMatchDataSet("clark-kent-without-nickname.yml")
    public void should_compare_null_value_defined_in_yaml_data_set() throws Exception {
        // given
        UserAccount clarkKent = em.find(UserAccount.class, 2L);

        // when
        clarkKent.setNickname(null);
        clarkKent = em.merge(clarkKent);

        // then
        // verified by DataSet comparison
    }

    @Test
    @CleanupUsingScript("delete-users.sql")
    @ApplyScriptBefore("clark-kent-with-nickname.sql")
    @ShouldMatchDataSet("clark-kent-without-nickname.yml")
    public void should_clean_database_before_test_using_custom_script() throws Exception {
        // given
        UserAccount clarkKent = em.find(UserAccount.class, 2L);

        // when
        clarkKent.setNickname(null);
        clarkKent = em.merge(clarkKent);

        // then
        // verified by DataSet comparison
    }

    @Test
    @ApplyScriptBefore("clark-kent-with-nickname.sql")
    @ShouldMatchDataSet("clark-kent-without-nickname.json")
    public void should_compare_null_value_defined_in_json_data_set() throws Exception {
        // given
        UserAccount clarkKent = em.find(UserAccount.class, 2L);

        // when
        clarkKent.setNickname(null);
        clarkKent = em.merge(clarkKent);

        // then
        // verified by DataSet comparison
    }

    @Test
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet("empty/empty.json")
    public void should_remove_all_users() throws Exception {
        // when
        em.createQuery("delete from UserAccount u").executeUpdate();

        // then
        // verified by DataSet comparison
    }

}
