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

import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.DataHandler;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitDataSetHandlingException;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

public class DBUnitDatasetHandler implements DataHandler
{

   @Inject @TestScoped
   private Instance<DatabaseConnection> databaseConnection;
   
   @Inject @TestScoped
   private Instance<DataSetRegister> dataSetRegister;
   
   @Inject @SuiteScoped
   private Instance<PersistenceConfiguration> configuration;
   
   @Override
   public void prepare(@Observes PrepareData prepareDataEvent)
   {
      try
      {
         applyInitStatement();
         fillDatabase();
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }
   }

   @Override
   public void compare(@Observes CompareData compareDataEvent)
   {
      try
      {
         IDataSet currentDataSet = databaseConnection.get().createDataSet();
         IDataSet expectedDataSet = DataSetUtils.mergeDataSets(dataSetRegister.get().getExpected());
         String[] tableNames = expectedDataSet.getTableNames();
         for (String tableName : tableNames)
         {
            ITable currentTableState = currentDataSet.getTable(tableName);
            ITable expectedTableState = expectedDataSet.getTable(tableName);
            String[] columnsToIgnore = DataSetUtils.columnsNotSpecifiedInExpectedDataSet(expectedTableState,currentTableState);
            Assertion.assertEqualsIgnoreCols(expectedTableState, currentTableState, columnsToIgnore);
         }
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }
   }
   
   @Override
   public void cleanup(@Observes CleanUpData cleanupDataEvent)
   {
      try
      {
         cleanDatabase();
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }
   }

   private void applyInitStatement()
   {
      PersistenceConfiguration persistenceConfiguration = configuration.get();
      if (!persistenceConfiguration.isInitStatementDefined())
      {
         return;
      }
      Statement initStatement = null;
      try
      {
         initStatement = databaseConnection.get().getConnection().createStatement();
         initStatement.execute(persistenceConfiguration.getInitStatement());
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      } 
      finally 
      {
         if (initStatement != null)
         {
            try
            {
               initStatement.close();
            }
            catch (SQLException e)
            {
               throw new DBUnitDataSetHandlingException("Unable to close init statement", e);
            }
         }
      }
   }

   private void fillDatabase() throws Exception
   {
      final DatabaseConnection connection = databaseConnection.get();
      IDataSet initialDataSet = DataSetUtils.mergeDataSets(dataSetRegister.get().getInitial());
      new TransactionOperation(DatabaseOperation.CLEAN_INSERT).execute(connection, initialDataSet);
   }

   private void cleanDatabase() throws Exception
   {
      DatabaseConnection connection = databaseConnection.get();
      IDataSet dataSet = connection.createDataSet();
      new TransactionOperation(DatabaseOperation.DELETE_ALL).execute(connection, dataSet);
   }
   
}
