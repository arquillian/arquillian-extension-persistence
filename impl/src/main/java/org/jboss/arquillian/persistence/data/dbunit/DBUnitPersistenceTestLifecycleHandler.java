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
package org.jboss.arquillian.persistence.data.dbunit;

import java.util.List;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitConnectionException;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitInitializationException;
import org.jboss.arquillian.persistence.data.descriptor.DataSetDescriptor;
import org.jboss.arquillian.persistence.data.descriptor.Format;
import org.jboss.arquillian.persistence.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

/**
 *
 * @author Bartosz Majsak
 *
 */
public class DBUnitPersistenceTestLifecycleHandler
{

   @Inject
   private Instance<DataSource> databaseSourceInstance;

   @Inject @TestScoped
   private InstanceProducer<DatabaseConnection> databaseConnectionProducer;

   @Inject @TestScoped
   private InstanceProducer<DataSetRegister> dataSetRegisterProducer;

   // ------------------------------------------------------------------------------------------------
   // Intercepting data handling events
   // ------------------------------------------------------------------------------------------------

   public void createDatabaseConnection(@Observes(precedence = 1000) EventContext<BeforePersistenceTest> context)
   {
      if (databaseConnectionProducer.get() == null)
      {
         createDatabaseConnection();
      }
      context.proceed();
   }

   public void closeConnection(@Observes(precedence = 1000) EventContext<AfterPersistenceTest> context)
   {
      try
      {
         context.proceed();
         databaseConnectionProducer.get().getConnection().close();
      }
      catch (Exception e)
      {
         throw new DBUnitConnectionException("Unable to close connection.", e);
      }
   }

   public void initializeDataSeeding(@Observes(precedence = 1000) EventContext<PrepareData> context)
   {
      PrepareData prepareDataEvent = context.getEvent();
      createInitialDataSets(prepareDataEvent.getDescriptors());
      context.proceed();
   }

   public void initializeDataVerification(@Observes(precedence = 1000) EventContext<CompareData> context)
   {
      CompareData compareDataEvent = context.getEvent();
      createExpectedDataSets(compareDataEvent.getDescriptors());
      context.proceed();
   }

   // ------------------------------------------------------------------------------------------------

   private void createDatabaseConnection()
   {
      try
      {
         DataSource dataSource = databaseSourceInstance.get();
         DatabaseConnection databaseConnection = new DatabaseConnection(dataSource.getConnection());
         databaseConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
               new DefaultDataTypeFactory());
         databaseConnectionProducer.set(databaseConnection);
      }
      catch (Exception e)
      {
         throw new DBUnitInitializationException("Unable to initialize database connection for dbunit module.", e);
      }
   }

   private void createInitialDataSets(List<DataSetDescriptor> dataSetDescriptors)
   {
      DataSetRegister dataSetRegister = getOrCreateDataSetRegister();
      for (DataSetDescriptor dataSetDescriptor : dataSetDescriptors)
      {
         IDataSet initialDataSet = createInitialDataSet(dataSetDescriptor);
         dataSetRegister.addInitial(initialDataSet);
      }
      dataSetRegisterProducer.set(dataSetRegister);
   }

   private IDataSet createInitialDataSet(DataSetDescriptor dataSetDescriptor)
   {
      final String file = dataSetDescriptor.getLocation();
      final Format format = dataSetDescriptor.getFormat();
      return DataSetBuilder.builderFor(format).build(file);
   }

   private void createExpectedDataSets(List<DataSetDescriptor> dataSetDescriptors)
   {
      DataSetRegister dataSetRegister = getOrCreateDataSetRegister();
      for (DataSetDescriptor dataSetDescriptor : dataSetDescriptors)
      {
         IDataSet expectedDataSet = createExpectedDataSet(dataSetDescriptor);
         dataSetRegister.addExpected(expectedDataSet);
      }
      dataSetRegisterProducer.set(dataSetRegister);
   }

   private IDataSet createExpectedDataSet(DataSetDescriptor dataSetDescriptor)
   {
      String file = dataSetDescriptor.getLocation();
      Format format = dataSetDescriptor.getFormat();
      return DataSetBuilder.builderFor(format).build(file);
   }

   private DataSetRegister getOrCreateDataSetRegister()
   {
      DataSetRegister dataSetRegister = dataSetRegisterProducer.get();
      if (dataSetRegister == null)
      {
         dataSetRegister = new DataSetRegister();
      }
      return dataSetRegister;
   }

}
