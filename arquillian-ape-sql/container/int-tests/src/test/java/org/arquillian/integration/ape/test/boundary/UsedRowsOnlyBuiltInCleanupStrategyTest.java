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
package org.arquillian.integration.ape.test.boundary;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.arquillian.ape.api.Cleanup;
import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.api.TestExecutionPhase;
import org.arquillian.integration.ape.example.UserAccount;
import org.arquillian.integration.ape.util.Query;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.arquillian.ape.rdbms.BuiltInCleanupStrategy.USED_ROWS_ONLY;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class UsedRowsOnlyBuiltInCleanupStrategyTest {

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
    @ApplyScriptBefore({"clark-kent-without-nickname.sql", "john-smith.sql"})
    @Cleanup(phase = TestExecutionPhase.NONE)
    public void insert_data_for_test() {
    }

    @Test
    @InSequence(2)
    @UsingDataSet("users.yml") // both users are without nicknames
    @Cleanup(phase = TestExecutionPhase.BEFORE)
    @CleanupStrategy(USED_ROWS_ONLY)
    public void should_cleanup_database_content_matching_users_from_dataset() {
        UserAccount clarkKent = em.find(UserAccount.class, 2L);
        assertThat(clarkKent.getNickname()).isNullOrEmpty();
    }
}
