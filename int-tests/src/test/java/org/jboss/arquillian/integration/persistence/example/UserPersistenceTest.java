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
package org.jboss.arquillian.integration.persistence.example;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test is using {@link PersistenceTest} annotation which is marker
 * annotation for triggering persistence extension. All tests within
 * marked test class are wrapped in transaction.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
@RunWith(Arquillian.class)
@PersistenceTest
public class UserPersistenceTest {

    @PersistenceContext
    EntityManager em;

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackage(UserAccount.class.getPackage())
            .addClass(Query.class)
            // required for remote containers in order to run tests with FEST-Asserts
            .addPackages(true, "org.assertj.core")
            .addPackages(true,  "org.apache.tools")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
    }

    @Test
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
}
