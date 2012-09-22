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

import java.sql.SQLException;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ExcludeTableFilter;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.DataSeedStrategy;
import org.jboss.arquillian.persistence.core.data.DataHandler;
import org.jboss.arquillian.persistence.core.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.core.data.script.ScriptExecutor;
import org.jboss.arquillian.persistence.core.event.CleanupData;
import org.jboss.arquillian.persistence.core.event.CleanupDataUsingScript;
import org.jboss.arquillian.persistence.core.event.CompareData;
import org.jboss.arquillian.persistence.core.event.ExecuteScripts;
import org.jboss.arquillian.persistence.core.event.PrepareData;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionFeatureResolver;
import org.jboss.arquillian.persistence.core.test.AssertionErrorCollector;
import org.jboss.arquillian.persistence.dbunit.cleanup.CleanupStrategyExecutor;
import org.jboss.arquillian.persistence.dbunit.cleanup.CleanupStrategyProvider;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitDataSeedStrategyProvider;
import org.jboss.arquillian.persistence.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.dbunit.exception.DBUnitConnectionException;
import org.jboss.arquillian.persistence.dbunit.exception.DBUnitDataSetHandlingException;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DBUnitDataHandler implements DataHandler
{

   @Inject
   private Instance<DatabaseConnection> databaseConnection;

   @Inject
   private Instance<DataSetRegister> dataSetRegister;

   @Inject
   private Instance<AssertionErrorCollector> assertionErrorCollector;

   @Inject
   private Instance<DBUnitConfiguration> dbunitConfigurationInstance;

   @Inject
   private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolverInstance;

   @Override
   public void prepare(@Observes PrepareData prepareDataEvent)
   {
      try
      {
         fillDatabase();
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException("Failed while seeding database.", e);
      }
   }

   @Override
   public void compare(@Observes CompareData compareDataEvent)
   {
      try
      {
         IDataSet currentDataSet = databaseConnection.get().createDataSet();
         final String[] excludeTablesFromComparisonWhenEmptyExpected = dbunitConfigurationInstance.get().getExcludeTablesFromComparisonWhenEmptyExpected();
         if (excludeTablesFromComparisonWhenEmptyExpected.length != 0)
         {
            currentDataSet = new FilteredDataSet(new ExcludeTableFilter(excludeTablesFromComparisonWhenEmptyExpected), currentDataSet);
         }
         final IDataSet expectedDataSet = DataSetUtils.mergeDataSets(dataSetRegister.get().getExpected());
         new DataSetComparator(compareDataEvent.getSortByColumns(), compareDataEvent.getColumnsToExclude()).compare(currentDataSet, expectedDataSet,
               assertionErrorCollector.get());
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException("Failed while comparing database state with provided data sets.", e);
      }
   }

   @Override
   public void cleanup(@Observes CleanupData cleanupDataEvent)
   {
      cleanDatabase(cleanupDataEvent.cleanupStrategy);
   }

   public void cleanupUsingScript(@Observes CleanupDataUsingScript cleanupDataUsingScriptEvent)
   {
      for (SqlScriptResourceDescriptor scriptDescriptor : cleanupDataUsingScriptEvent.getDescriptors())
      {
         final String script = scriptDescriptor.getContent();
         executeScript(script);
      }
   }

   public void executeScripts(@Observes ExecuteScripts executeScriptsEvent)
   {
      for (SqlScriptResourceDescriptor scriptDescriptor : executeScriptsEvent.getDescriptors())
      {
         final String script = scriptDescriptor.getContent();
         executeScript(script);
      }

   }

   // -- Private methods

   private void executeScript(String script)
   {
      try
      {
         new ScriptExecutor(databaseConnection.get().getConnection()).execute(script);
      }
      catch (SQLException e)
      {
         throw new DBUnitConnectionException("Unable to obtain JDBC connection", e);
      }
   }

   private void fillDatabase() throws Exception
   {
      final IDataSet initialDataSet = DataSetUtils.mergeDataSets(dataSetRegister.get().getInitial());
      final DatabaseOperation selectedSeedingStrategy = getSelectedSeedingStrategy();
      new TransactionOperation(selectedSeedingStrategy).execute(databaseConnection.get(), initialDataSet);
   }

   private DatabaseOperation getSelectedSeedingStrategy()
   {
      final DBUnitConfiguration dbUnitConfiguration = dbunitConfigurationInstance.get();
      final DataSeedStrategy dataSeedStrategy = persistenceExtensionFeatureResolverInstance.get().getDataSeedStrategy();
      final boolean useIdentityInsert = dbUnitConfiguration.isUseIdentityInsert();
      final DatabaseOperation selectedSeedingStrategy = dataSeedStrategy.provide(new DBUnitDataSeedStrategyProvider(
            useIdentityInsert));
      return selectedSeedingStrategy;
   }

   private void cleanDatabase(CleanupStrategy cleanupStrategy)
   {
      final CleanupStrategyExecutor cleanupStrategyExecutor = cleanupStrategy.provide(new CleanupStrategyProvider(
            databaseConnection.get(), dataSetRegister.get()));
      cleanupStrategyExecutor.cleanupDatabase(dbunitConfigurationInstance.get().getExcludeTablesFromCleanup());
   }

}
