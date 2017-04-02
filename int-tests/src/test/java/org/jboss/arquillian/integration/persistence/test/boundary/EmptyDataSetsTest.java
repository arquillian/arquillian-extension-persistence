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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.dbunit.assertion.DbComparisonFailure;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.testextension.exception.ShouldFailWith;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.integration.persistence.util.UserPersistenceAssertion;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * All tests are wrapped in transaction.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE)
public class EmptyDataSetsTest {

    @PersistenceContext
    private EntityManager em;

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackage(UserAccount.class.getPackage())
            .addClasses(Query.class, UserPersistenceAssertion.class)
            // required for remote containers in order to run tests with FEST-Asserts
            .addPackages(true, "org.assertj.core")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
    }

    @Test
    @UsingDataSet("empty/empty.yml")
    public void should_skip_empty_yaml_data_set() throws Exception {
        new UserPersistenceAssertion(em).assertNoUserAccountsStored();
    }

    @Test
    @UsingDataSet("empty/empty.json")
    public void should_skip_empty_json_data_set() throws Exception {
        new UserPersistenceAssertion(em).assertNoUserAccountsStored();
    }

    @Test
    @UsingDataSet("empty/empty.xml")
    public void should_skip_empty_xml_data_set() throws Exception {
        new UserPersistenceAssertion(em).assertNoUserAccountsStored();
    }

    @Test
    @UsingDataSet("empty/empty.xls")
    public void should_skip_empty_xls_data_set() throws Exception {
        new UserPersistenceAssertion(em).assertNoUserAccountsStored();
    }

    @Test
    @UsingDataSet("empty/empty-tables.yml")
    public void should_clean_when_yaml_with_empty_tables_provided() throws Exception {
        new UserPersistenceAssertion(em).assertNoUserAccountsStored();
    }

    @Test
    @ShouldFailWith(AssertionError.class)
    @UsingDataSet("users.json")
    @ShouldMatchDataSet("empty/empty.yml")
    public void should_fail_when_empty_set_yaml_used_for_verifying_content_of_non_empty_database() {
        new UserPersistenceAssertion(em).assertUserAccountsStored();
    }

    @Test
    @ShouldFailWith(AssertionError.class)
    @UsingDataSet("users.yml")
    @ShouldMatchDataSet("empty/empty.json")
    public void should_fail_when_empty_set_json_used_for_verifying_content_of_non_empty_database() throws Exception {
        new UserPersistenceAssertion(em).assertUserAccountsStored();
    }

    @Test
    @ShouldFailWith(DbComparisonFailure.class)
    @UsingDataSet("users.xml")
    @ShouldMatchDataSet("empty/empty.xls")
    public void should_fail_when_empty_set_xls_used_for_verifying_content_of_non_empty_database() throws Exception {
        new UserPersistenceAssertion(em).assertUserAccountsStored();
    }

    @Test
    @ShouldFailWith(AssertionError.class)
    @UsingDataSet("users.xml")
    @ShouldMatchDataSet("empty/empty.xml")
    public void should_fail_when_empty_set_xml_used_for_verifying_content_of_non_empty_database() throws Exception {
        new UserPersistenceAssertion(em).assertUserAccountsStored();
    }
}
