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
package org.arquillian.integration.ape.test.customscripts;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.arquillian.ape.rdbms.ShouldMatchDataSet;
import org.arquillian.ape.api.TestExecutionPhase;
import org.arquillian.integration.ape.example.UserAccount;
import org.arquillian.integration.ape.testextension.event.annotation.ExecuteScriptsShouldBeTriggered;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ApplyingCustomScriptsTest {

    @PersistenceContext
    EntityManager em;

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addPackage(UserAccount.class.getPackage())
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("test-persistence.xml", "persistence.xml");
    }

    @Test
    @ApplyScriptBefore("users.sql")
    @ShouldMatchDataSet(value = "two-users.yml", excludeColumns = "id")
    @ExecuteScriptsShouldBeTriggered(TestExecutionPhase.BEFORE)
    public void should_add_users_before_test_using_custom_script() throws Exception {
    }

    @Test
    @ApplyScriptBefore({
        "INSERT INTO useraccount (id, firstname, lastname, username, password) VALUES (1, 'John', 'Smith', 'doovde', 'password')",
        "INSERT INTO useraccount (id, firstname, lastname, username, password) VALUES (2, 'Clark', 'Kent', 'superman', 'kryptonite')"})
    @ShouldMatchDataSet(value = "two-users.yml", excludeColumns = "id")
    @ExecuteScriptsShouldBeTriggered(TestExecutionPhase.BEFORE)
    public void should_add_users_before_test_using_inline_script() throws Exception {
    }

    @Test
    @ApplyScriptBefore("clark-kent.sql")
    @ShouldMatchDataSet(value = "two-users.yml", excludeColumns = "id")
    @ExecuteScriptsShouldBeTriggered(value = TestExecutionPhase.BEFORE)
    public void should_add_user_to_already_created_entries_using_custom_script() throws Exception {
        // given
        UserAccount johnSmith = new UserAccount("John", "Smith", "doovde", "password");

        // when
        em.persist(johnSmith);

        // then
        // superman should be added before test execution
        // and data should be compared using dataset defined in @ShouldMatchDataSet
    }
}
