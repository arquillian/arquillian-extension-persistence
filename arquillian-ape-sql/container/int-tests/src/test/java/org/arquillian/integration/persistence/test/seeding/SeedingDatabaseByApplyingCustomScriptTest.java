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
package org.jboss.arquillian.integration.persistence.test.seeding;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.testextension.event.annotation.ExecuteScriptsShouldBeTriggered;
import org.jboss.arquillian.junit.Arquillian;
import org.arquillian.persistence.ApplyScriptBefore;
import org.arquillian.persistence.ShouldMatchDataSet;
import org.arquillian.persistence.TestExecutionPhase;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SeedingDatabaseByApplyingCustomScriptTest {

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(UserAccount.class.getPackage())
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource("test-persistence.xml", "persistence.xml");
    }

    @Test
    @ApplyScriptBefore("users.sql")
    @ShouldMatchDataSet("two-users.yml")
    @ExecuteScriptsShouldBeTriggered(TestExecutionPhase.BEFORE)
    public void should_seed_database_using_custom_script() throws Exception {
    }
}
