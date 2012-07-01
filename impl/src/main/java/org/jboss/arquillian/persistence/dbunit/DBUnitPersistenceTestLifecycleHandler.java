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
package org.jboss.arquillian.persistence.dbunit;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.persistence.core.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.core.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.core.event.CompareData;
import org.jboss.arquillian.persistence.core.event.PrepareData;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionFeatureResolver;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfigurationPropertyMapper;
import org.jboss.arquillian.persistence.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.jboss.arquillian.persistence.dbunit.data.provider.DataSetProvider;
import org.jboss.arquillian.persistence.dbunit.data.provider.ExpectedDataSetProvider;
import org.jboss.arquillian.persistence.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.dbunit.exception.DBUnitConnectionException;
import org.jboss.arquillian.persistence.dbunit.exception.DBUnitInitializationException;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

/**
 *
 * @author Bartosz Majsak
 *
 */
public class DBUnitPersistenceTestLifecycleHandler
{

   @Inject
   private Instance<DataSource> dataSourceInstance;

   @Inject
   private Instance<MetadataExtractor> metadataExtractorInstance;

   @Inject
   private Instance<DBUnitConfiguration> dbUnitConfigurationInstance;

   @Inject @TestScoped
   private InstanceProducer<DatabaseConnection> databaseConnectionProducer;

   @Inject @TestScoped
   private InstanceProducer<DataSetRegister> dataSetRegisterProducer;

   @Inject
   private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolverInstance;

   // ------------------------------------------------------------------------------------------------
   // Intercepting data handling events
   // ------------------------------------------------------------------------------------------------

   public void createDatabaseConnection(@Observes(precedence = 1000) EventContext<BeforePersistenceTest> context)
   {
      if (databaseConnectionProducer.get() == null)
      {
         createDatabaseConnection();
         configure();
      }

      final Method testMethod = context.getEvent().getTestMethod();

      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = persistenceExtensionFeatureResolverInstance.get();
      if (persistenceExtensionFeatureResolver.shouldSeedData())
      {
         DataSetProvider dataSetProvider = new DataSetProvider(metadataExtractorInstance.get(), dbUnitConfigurationInstance.get());
         createInitialDataSets(dataSetProvider.getDescriptorsDefinedFor(testMethod));
      }

      if (persistenceExtensionFeatureResolver.shouldVerifyDataAfterTest()) {
         final ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(metadataExtractorInstance.get(), dbUnitConfigurationInstance.get());
         createExpectedDataSets(dataSetProvider.getDescriptorsDefinedFor(testMethod));
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

   // ------------------------------------------------------------------------------------------------

   private void createDatabaseConnection()
   {
      try
      {
         DataSource dataSource = dataSourceInstance.get();
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

   private void createInitialDataSets(Collection<DataSetResourceDescriptor> dataSetDescriptors)
   {
      DataSetRegister dataSetRegister = getOrCreateDataSetRegister();
      for (DataSetResourceDescriptor dataSetDescriptor : dataSetDescriptors)
      {
         dataSetRegister.addInitial(dataSetDescriptor.getContent());
      }
      dataSetRegisterProducer.set(dataSetRegister);
   }

   private void createExpectedDataSets(Collection<DataSetResourceDescriptor> dataSetDescriptors)
   {
      DataSetRegister dataSetRegister = getOrCreateDataSetRegister();
      for (DataSetResourceDescriptor dataSetDescriptor : dataSetDescriptors)
      {
         dataSetRegister.addExpected(dataSetDescriptor.getContent());
      }
      dataSetRegisterProducer.set(dataSetRegister);
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

   private void configure()
   {
      final DatabaseConnection databaseConnection = databaseConnectionProducer.get();
      final DatabaseConfig dbUnitConfig = databaseConnection.getConfig();

      final Map<String, Object> properties = new DBUnitConfigurationPropertyMapper().map(dbUnitConfigurationInstance.get());
      for (Entry<String, Object> property : properties.entrySet())
      {
         dbUnitConfig.setProperty(property.getKey(), property.getValue());
      }
   }

}
