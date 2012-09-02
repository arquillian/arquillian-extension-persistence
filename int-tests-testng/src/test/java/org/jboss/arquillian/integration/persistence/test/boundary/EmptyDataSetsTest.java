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

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.integration.persistence.example.UserAccount;
import org.jboss.arquillian.integration.persistence.util.Query;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

/**
 *
 * All tests are wrapped in transaction.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@Transactional
public class EmptyDataSetsTest extends Arquillian
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
                       .addPackage(UserAccount.class.getPackage())
                       .addClass(Query.class)
                       // required for remote containers in order to run tests with FEST-Asserts
                       .addPackages(true, "org.fest")
                       .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
   }

   @PersistenceContext
   EntityManager em;

   @Test
   @UsingDataSet("empty/empty.yml")
   public void should_skip_empty_yaml_data_set() throws Exception
   {
      assertNoUserAccountsStored();
   }

   @Test
   @UsingDataSet("empty/empty.json")
   public void should_skip_empty_json_data_set() throws Exception
   {
      assertNoUserAccountsStored();
   }

   @Test
   @UsingDataSet("empty/empty.xml")
   public void should_skip_empty_xml_data_set() throws Exception
   {
      assertNoUserAccountsStored();
   }

   @Test
   @UsingDataSet("empty/empty.xls")
   public void should_skip_empty_xls_data_set() throws Exception
   {
      assertNoUserAccountsStored();
   }

   // Private helper methods

   private void assertNoUserAccountsStored()
   {
      @SuppressWarnings("unchecked")
      List<UserAccount> savedUserAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();
      assertThat(savedUserAccounts).isEmpty();
   }

}
