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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.Address;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.testextension.data.annotation.DatabaseShouldBeEmptyAfterTest;
import org.jboss.arquillian.integration.persistence.testextension.data.annotation.DatabaseShouldContainAfterTest;
import org.jboss.arquillian.integration.persistence.testextension.data.annotation.ShouldBeEmptyAfterTest;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.arquillian.persistence.BuiltInCleanupStrategy.STRICT;
import static org.jboss.arquillian.persistence.BuiltInCleanupStrategy.USED_ROWS_ONLY;
import static org.jboss.arquillian.persistence.BuiltInCleanupStrategy.USED_TABLES_ONLY;

@RunWith(Arquillian.class)
public class DataCleanupStrategiesTest {

    @PersistenceContext
    EntityManager em;

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addPackage(UserAccount.class.getPackage())
            .addClass(Query.class)
            // required for remote containers in order to run tests with FEST-Asserts
            .addPackages(true, "org.assertj.core")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("test-persistence.xml", "persistence.xml");
    }

    @Test
    @InSequence(1)
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.AFTER)
    @CleanupStrategy(STRICT)
    @DatabaseShouldBeEmptyAfterTest
    public void should_cleanup_whole_database_content_when_using_strict_mode() {
        em.persist(new Address("Kryptonite", 1, "Metropolis", 7272));
    }

    @Test
    @InSequence(2)
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.AFTER)
    @CleanupStrategy(USED_ROWS_ONLY)
    @DatabaseShouldContainAfterTest("expected-address.yml")
    public void should_cleanup_entries_added_using_data_set() {
        em.persist(new Address("Testing Street", 7, "JavaPolis", 1234));
    }

    @Test
    @InSequence(3)
    @ApplyScriptBefore({"delete-all.sql", "one-address.sql"})
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet(value = {"users.yml", "lex-luthor.yml", "expected-address.yml"}, excludeColumns = {"id"})
    @Cleanup(phase = TestExecutionPhase.AFTER)
    @CleanupStrategy(USED_ROWS_ONLY)
    @DatabaseShouldContainAfterTest({"expected-address.yml", "lex-luthor.yml"})
    public void should_cleanup_entries_added_using_data_set_but_not_by_script() {
        em.persist(new UserAccount("Lex", "Luthor", "LexCorp", "Injustice Gang"));
    }

    @Test
    @InSequence(4)
    @ApplyScriptBefore({"delete-all.sql", "one-address.sql", "lex-luthor.sql"})
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.AFTER)
    @CleanupStrategy(USED_TABLES_ONLY)
    @DatabaseShouldContainAfterTest({"expected-address.yml"})
    @ShouldBeEmptyAfterTest("useraccount")
    public void should_cleanup_all_tables_defined_in_data_set() {
        em.persist(new UserAccount("Bartosz", "Majsak", "fonejacker", "doovdePUK"));
    }

    @Test
    @InSequence(5)
    @ApplyScriptBefore({"delete-all.sql", "one-address.sql", "lex-luthor.sql"})
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.AFTER)
    @CleanupStrategy(USED_TABLES_ONLY)
    @DatabaseShouldContainAfterTest({"expected-address.yml"})
    @ShouldBeEmptyAfterTest("useraccount")
    public void should_seed_using_both_custom_scripts_and_datasets_and_cleanup_all_tables_defined_in_data_set() {
        final List<UserAccount> users =
            (List<UserAccount>) em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();
        final List<Address> addresses = em.createQuery(Query.selectAllInJPQL(Address.class)).getResultList();

        assertThat(users).hasSize(3);
        assertThat(addresses).hasSize(1);
    }
}
