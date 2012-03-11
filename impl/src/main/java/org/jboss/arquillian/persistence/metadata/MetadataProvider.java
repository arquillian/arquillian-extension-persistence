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
package org.jboss.arquillian.persistence.metadata;

import java.lang.reflect.Method;

import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class MetadataProvider
{

   private final PersistenceConfiguration configuration;

   private final MetadataExtractor metadataExtractor;

   private final Method testMethod;

   public MetadataProvider(Method testMethod, MetadataExtractor metadataExtractor, PersistenceConfiguration configuration)
   {
      this.metadataExtractor = metadataExtractor;
      this.configuration = configuration;
      this.testMethod = testMethod;
   }

   // ---------------------------------------------------------------------------------------------------
   // Public API methods
   // ---------------------------------------------------------------------------------------------------

   public boolean isDataSeedOperationRequested()
   {
      return metadataExtractor.usingDataSet().isDefinedOnClassLevel()
            || metadataExtractor.usingDataSet().isDefinedOn(testMethod);
   }

   public boolean isCustomScriptToBeAppliedBeforeTestRequested()
   {
      return  metadataExtractor.applyScriptBefore().isDefinedOnClassLevel()
            || metadataExtractor.applyScriptBefore().isDefinedOn(testMethod);
   }

   public boolean isCustomScriptToBeAppliedAfterTestRequested()
   {
      return  metadataExtractor.applyScriptAfter().isDefinedOnClassLevel()
            || metadataExtractor.applyScriptAfter().isDefinedOn(testMethod);
   }

   public boolean isDataVerificationRequested()
   {
      return metadataExtractor.shouldMatchDataSet().isDefinedOnClassLevel()
            || metadataExtractor.shouldMatchDataSet().isDefinedOn(testMethod);
   }

   public boolean isTransactional()
   {
      return !TransactionMode.DISABLED.equals(getTransactionalMode());
   }

   public TransactionMode getTransactionalMode()
   {
      final Transactional transactionalAnnotation = metadataExtractor.transactional().fetchUsingFirst(testMethod);

      TransactionMode mode = configuration.getDefaultTransactionMode();
      if (transactionalAnnotation != null)
      {
         mode = transactionalAnnotation.value();
      }

      return mode;
   }

   public TestExecutionPhase getCleanupTestPhase()
   {
      final Cleanup cleanupAnnotation = metadataExtractor.cleanup().fetchUsingFirst(testMethod);

      TestExecutionPhase phase = TestExecutionPhase.getDefault();
      if (cleanupAnnotation != null)
      {
         phase = cleanupAnnotation.phase();
      }

      return phase;
   }

   public boolean shouldCleanupBefore()
   {
      return TestExecutionPhase.BEFORE.equals(getCleanupTestPhase());
   }

   public boolean shouldCleanupAfter()
   {
      return TestExecutionPhase.AFTER.equals(getCleanupTestPhase());
   }

   public String getDataSourceName()
   {
      String dataSource = "";

      if (configuration.isDefaultDataSourceDefined())
      {
         dataSource = configuration.getDefaultDataSource();
      }

      final DataSource dataSourceAnnotation = metadataExtractor.dataSource().fetchUsingFirst(testMethod);
      if (dataSourceAnnotation != null)
      {
         dataSource = dataSourceAnnotation.value();
      }

      if (dataSource.isEmpty())
      {
         throw new DataSourceNotDefinedException("DataSource not defined! Please declare in arquillian.xml or by using @DataSource annotation.");
      }

      return dataSource;
   }

}
