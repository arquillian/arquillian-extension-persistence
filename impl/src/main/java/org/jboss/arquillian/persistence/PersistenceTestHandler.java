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
package org.jboss.arquillian.persistence;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.persistence.event.TransactionFinished;
import org.jboss.arquillian.persistence.event.TransactionStarted;
import org.jboss.arquillian.persistence.exception.DataSourceNotFoundException;
import org.jboss.arquillian.persistence.metadata.DataSetProvider;
import org.jboss.arquillian.persistence.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.metadata.MetadataProvider;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

public class PersistenceTestHandler
{

   @Inject
   private Instance<PersistenceConfiguration> configuration;
   
   @Inject @ClassScoped
   private InstanceProducer<MetadataExtractor> metadataExtractor;
   
   @Inject @TestScoped
   private InstanceProducer<javax.sql.DataSource> dataSourceProducer;
   
   @Inject
   private Event<PrepareData> prepareDataEvent;
   
   @Inject
   private Event<CompareData> compareDataEvent;

   @Inject
   private Event<CleanUpData> cleanUpDataEvent;
   
   @Inject
   private Event<TransactionStarted> transactionStartedEvent;

   @Inject
   private Event<TransactionFinished> transactionFinishedEvent;
   
   @Inject
   private Instance<Context> contextInst;

   public void beforeSuite(@Observes BeforeClass beforeClass)
   {
      metadataExtractor.set(new MetadataExtractor(beforeClass.getTestClass()));
   }
   
   public void beforeTest(@Observes Before beforeTestEvent)
   {
      MetadataProvider metadataProvider = new MetadataProvider(beforeTestEvent.getTestMethod(), metadataExtractor.get(), configuration.get());
      if (!metadataProvider.isPersistenceFeatureEnabled())
      {
         return;
      }

      String dataSourceName = metadataProvider.getDataSourceName();
      dataSourceProducer.set(loadDataSource(dataSourceName));
      
      DataSetProvider dataSetProvider = new DataSetProvider(metadataExtractor.get(), configuration.get());
      prepareDataEvent.fire(new PrepareData(beforeTestEvent, dataSetProvider.getDataSetDescriptors(beforeTestEvent.getTestMethod())));
      
      if (metadataProvider.isTransactional())
      {
         transactionStartedEvent.fire(new TransactionStarted(beforeTestEvent));
      }
   }

   public void afterTest(@Observes After afterTestEvent)
   {
      MetadataProvider metadataProvider = new MetadataProvider(afterTestEvent.getTestMethod(), metadataExtractor.get(), configuration.get());
      if (!metadataProvider.isPersistenceFeatureEnabled())
      {
         return;
      }
      
      if (metadataProvider.isTransactional())
      {
         transactionFinishedEvent.fire(new TransactionFinished(afterTestEvent));
      }

      if (metadataProvider.isDataVerificationExpected())
      {
         DataSetProvider dataSetProvider = new DataSetProvider(metadataExtractor.get(), configuration.get());
         compareDataEvent.fire(new CompareData(afterTestEvent, dataSetProvider.getExpectedDataSetDescriptors(afterTestEvent.getTestMethod())));
      }
      
      cleanUpDataEvent.fire(new CleanUpData(afterTestEvent));
   }

   // Private methods
   
   private DataSource loadDataSource(String dataSourceName)
   {
      try
      {
         final Context context = contextInst.get();
         if(context == null)
         {
            throw new DataSourceNotFoundException("No Naming Context available");
         }
         return (javax.sql.DataSource) context.lookup(dataSourceName);
      }
      catch (NamingException e)
      {
         throw new DataSourceNotFoundException("Unable to find data source for given name: " + dataSourceName, e);
      }
   }

}
