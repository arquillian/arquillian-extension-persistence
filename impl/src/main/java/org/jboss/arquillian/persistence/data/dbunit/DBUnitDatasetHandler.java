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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedTable;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.data.DataHandler;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitDataSetHandlingException;
import org.jboss.arquillian.persistence.data.descriptor.SqlScriptDescriptor;
import org.jboss.arquillian.persistence.event.ApplyInitStatement;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.ExecuteScripts;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.persistence.test.AssertionErrorCollector;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DBUnitDatasetHandler implements DataHandler
{

   @Inject
   private Instance<DatabaseConnection> databaseConnection;

   @Inject
   private Instance<DataSetRegister> dataSetRegister;

   @Inject
   private Instance<AssertionErrorCollector> assertionErrorCollector;

   @Override
   public void initStatements(@Observes ApplyInitStatement applyInitStatementEvent)
   {
      final String initScript = applyInitStatementEvent.getInitStatement();
      if (initScript == null || initScript.isEmpty())
      {
         return;
      }
      executeScript(initScript);
   }

   @Override
   public void prepare(@Observes PrepareData prepareDataEvent)
   {
      try
      {
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
            SortedTable expectedTableState = new SortedTable(expectedDataSet.getTable(tableName));
            SortedTable currentTableState = new SortedTable(currentDataSet.getTable(tableName),
                  expectedTableState.getTableMetaData());
            String[] columnsToIgnore = DataSetUtils.columnsNotSpecifiedInExpectedDataSet(expectedTableState,
                  currentTableState);
            try
            {
               Assertion.assertEqualsIgnoreCols(expectedTableState, currentTableState, columnsToIgnore);
            }
            catch (AssertionError error)
            {
               assertionErrorCollector.get().collect(error);
            }

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

   @Override
   public void executeScripts(@Observes ExecuteScripts executeScriptsEvent)
   {
      for (SqlScriptDescriptor scriptDescriptor : executeScriptsEvent.getDescriptors())
      {
         final String script = loadScript(scriptDescriptor.getLocation());
         executeScript(script);
      }

   }

   // Private methods

   private String loadScript(String location)
   {
      final StringBuilder builder = new StringBuilder();

      final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);

      BufferedReader reader = null;
      String line = null;

      try
      {
         reader = new BufferedReader(new InputStreamReader(inputStream));
         while ((line  = reader.readLine()) != null)
         {
            builder.append(' ').append(line);
         }
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }
      finally
      {
         if (reader != null)
         {
            try
            {
               reader.close();
            }
            catch (IOException e)
            {
               throw new DBUnitDataSetHandlingException("Unable to close script.", e);
            }
         }
      }

      return builder.toString();
   }

   private void executeScript(String script)
   {
      Statement statement = null;
      try
      {
         statement = databaseConnection.get().getConnection().createStatement();
         statement.execute(script);
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }
      finally
      {
         if (statement != null)
         {
            try
            {
               statement.close();
            }
            catch (SQLException e)
            {
               throw new DBUnitDataSetHandlingException("Unable to close statement", e);
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
