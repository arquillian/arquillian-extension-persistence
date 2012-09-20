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
package org.jboss.arquillian.integration.persistence.testextension.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.integration.persistence.testextension.data.annotation.DatabaseShouldBeEmptyAfterTest;
import org.jboss.arquillian.integration.persistence.testextension.data.annotation.DatabaseShouldContainAfterTest;
import org.jboss.arquillian.integration.persistence.testextension.data.annotation.ShouldBeEmptyAfterTest;
import org.jboss.arquillian.persistence.core.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.core.test.AssertionErrorCollector;
import org.jboss.arquillian.persistence.dbunit.DataSetComparator;
import org.jboss.arquillian.persistence.dbunit.DataSetUtils;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.jboss.arquillian.test.spi.TestClass;

public class DataContentVerifier
{

   @Inject
   private Instance<DatabaseConnection> databaseConnection;

   @Inject
   private Instance<DBUnitConfiguration> dbunitConfiguration;

   @Inject
   private Instance<MetadataExtractor> metadataExtractor;

   @Inject
   private Instance<AssertionErrorCollector> assertionErrorCollector;

   public void verifyDatabaseContentAfterTest(@Observes(precedence = -1000) AfterPersistenceTest afterPersistenceTest)
   {
      final DataSetComparator dataSetComparator = new DataSetComparator(new String[] {}, new String[] {});
      try
      {
         Method testMethod = afterPersistenceTest.getTestMethod();
         DatabaseShouldBeEmptyAfterTest shouldBeEmptyAfterTest = testMethod.getAnnotation(DatabaseShouldBeEmptyAfterTest.class);
         final IDataSet actualContent = databaseConnection.get().createDataSet();
         if (shouldBeEmptyAfterTest != null)
         {
            final IDataSet filteredActualContent = DataSetUtils.excludeTables(actualContent, dbunitConfiguration.get().getExcludeTablesFromCleanup());
            dataSetComparator.shouldBeEmpty(filteredActualContent, assertionErrorCollector.get());
         }

         DatabaseShouldContainAfterTest databaseShouldContain = testMethod.getAnnotation(DatabaseShouldContainAfterTest.class);
         if (databaseShouldContain != null)
         {
            final IDataSet expectedDataSet = createExpectedDataSet(afterPersistenceTest);
            dataSetComparator.compare(actualContent, expectedDataSet, assertionErrorCollector.get());
         }

         ShouldBeEmptyAfterTest shouldBeEmpty = testMethod.getAnnotation(ShouldBeEmptyAfterTest.class);
         if (shouldBeEmpty != null)
         {
            final IDataSet expectedDataSet = actualContent;
            for (String tableName : shouldBeEmpty.value())
            {
               dataSetComparator.shouldBeEmpty(tableName, expectedDataSet, assertionErrorCollector.get());
            }
         }

      }
      catch (Exception e)
      {
         throw new RuntimeException("Unable to verify database content after test", e);
      }
   }

   private IDataSet createExpectedDataSet(AfterPersistenceTest afterPersistenceTest) throws DataSetException
   {
      TestClass testClass = afterPersistenceTest.getTestClass();
      CleanupVerificationDataSetProvider cleanupVerificationDataSetProvider = new CleanupVerificationDataSetProvider(testClass, metadataExtractor.get(), dbunitConfiguration.get());
      Collection<DataSetResourceDescriptor> descriptors = cleanupVerificationDataSetProvider.getDescriptorsDefinedFor(afterPersistenceTest.getTestMethod());
      List<IDataSet> dataSets = new ArrayList<IDataSet>(descriptors.size());
      for (DataSetResourceDescriptor dataSetDescriptor : descriptors)
      {
         dataSets.add(dataSetDescriptor.getContent());
      }
      IDataSet expectedDataSet = DataSetUtils.mergeDataSets(dataSets);
      return expectedDataSet;
   }

}
