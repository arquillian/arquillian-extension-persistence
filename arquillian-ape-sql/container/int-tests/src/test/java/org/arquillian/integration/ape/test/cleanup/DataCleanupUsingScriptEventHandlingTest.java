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
package org.arquillian.integration.ape.test.cleanup;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.arquillian.ape.rdbms.CleanupUsingScript;
import org.arquillian.ape.rdbms.ShouldMatchDataSet;
import org.arquillian.ape.api.TestExecutionPhase;
import org.arquillian.integration.ape.example.UserAccount;
import org.arquillian.integration.ape.testextension.event.annotation.CleanupShouldBeTriggered;
import org.arquillian.integration.ape.testextension.event.annotation.CleanupShouldNotBeTriggered;
import org.arquillian.integration.ape.testextension.event.annotation.CleanupUsingScriptShouldBeTriggered;
import org.arquillian.integration.ape.testextension.event.annotation.CleanupUsingScriptShouldNotBeTriggered;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
@RunWith(Arquillian.class)
public class DataCleanupUsingScriptEventHandlingTest {

    @PersistenceContext
    EntityManager em;

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackage(UserAccount.class.getPackage())
            // required for remote containers in order to run tests with FEST-Asserts
            .addPackages(true, "org.assertj.core")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
    }

    @Test
    @InSequence(1)
    @CleanupUsingScript(value = "delete-users.sql", phase = TestExecutionPhase.BEFORE)
    @CleanupShouldNotBeTriggered
    @CleanupUsingScriptShouldBeTriggered(TestExecutionPhase.BEFORE)
    public void should_cleanup_data_using_custom_sql_script_before_test() throws Exception {
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
    @InSequence(2)
    @CleanupUsingScript("delete-users.sql")
    @CleanupShouldNotBeTriggered
    @CleanupUsingScriptShouldBeTriggered(TestExecutionPhase.AFTER)
    public void should_cleanup_data_using_custom_sql_script_after_test_when_not_specified() throws Exception {
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
    @InSequence(3)
    @CleanupUsingScript(phase = TestExecutionPhase.NONE)
    @ShouldMatchDataSet("expected-users.json")
    @CleanupShouldBeTriggered(TestExecutionPhase.AFTER)
    @CleanupUsingScriptShouldNotBeTriggered
    public void should_not_infer_filename_when_cleanup_using_custom_sql_script_is_disabled() throws Exception {
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
