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

import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;

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

   public boolean isPersistenceFeatureEnabled()
   {
      return (hasDataAnnotation() || hasExpectedAnnotation() || hasPersistenceTestAnnotation());
   }

   public boolean isDataSeedOperationRequested()
   {
      return metadataExtractor.hasDataAnnotationOnClassLevel()
            || metadataExtractor.hasDataAnnotationOn(testMethod);
   }

   public boolean isDataVerificationRequested()
   {
      return metadataExtractor.hasExpectedAnnotationOnClassLevel()
            || metadataExtractor.hasExpectedAnnotationOn(testMethod);
   }

   public boolean isTransactional()
   {
      return !TransactionMode.DISABLED.equals(getTransactionalMode());
   }

   public TransactionMode getTransactionalMode()
   {
      Transactional transactionalAnnotation = metadataExtractor.getTransactionalAnnotationOnClassLevel();
      if (metadataExtractor.hasTransactionalAnnotationOn(testMethod))
      {
         transactionalAnnotation = metadataExtractor.getTransactionalAnnotationOn(testMethod);
      }

      TransactionMode mode = configuration.getDefaultTransactionMode();
      if (transactionalAnnotation != null)
      {
         mode = transactionalAnnotation.value();
      }
      return mode;
   }

   public String getDataSourceName()
   {
      String dataSource = "";

      if (configuration.isDefaultDataSourceDefined())
      {
         dataSource = configuration.getDefaultDataSource();
      }

      if (hasDataSourceAnnotation())
      {
         dataSource = getDataSourceAnnotation().value();
      }

      if (dataSource.isEmpty())
      {
         throw new DataSourceNotDefinedException("DataSource not defined");
      }

      return dataSource;
   }

   // ---------------------------------------------------------------------------------------------------
   // Internal methods
   // ---------------------------------------------------------------------------------------------------

   boolean hasDataAnnotation()
   {
      return metadataExtractor.hasDataAnnotationOnClassLevel()
            || metadataExtractor.hasDataAnnotationOn(testMethod);
   }

   private boolean hasExpectedAnnotation()
   {
      return metadataExtractor.hasExpectedAnnotationOnClassLevel()
            || metadataExtractor.hasExpectedAnnotationOn(testMethod);
   }

   private boolean hasPersistenceTestAnnotation()
   {
      return metadataExtractor.hasPersistenceTestAnnotation();
   }

   private boolean hasDataSourceAnnotation()
   {
      return metadataExtractor.hasDataSourceAnnotationOnClassLevel()
            || metadataExtractor.hasDataSourceAnnotationOn(testMethod);
   }

   private DataSource getDataSourceAnnotation()
   {
      DataSource usedAnnotation = metadataExtractor.getDataSourceAnnotationOnClassLevel();
      if (metadataExtractor.hasDataSourceAnnotationOn(testMethod))
      {
         usedAnnotation = metadataExtractor.getDataSourceAnnotationOn(testMethod);
      }

      return usedAnnotation;
   }

}
