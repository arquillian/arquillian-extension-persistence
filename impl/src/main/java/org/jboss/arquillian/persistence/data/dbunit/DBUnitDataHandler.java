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

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.data.DataHandler;
import org.jboss.arquillian.persistence.data.dbunit.cleanup.CleanupStrategyExecutor;
import org.jboss.arquillian.persistence.data.dbunit.cleanup.CleanupStrategyProvider;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitDataSetHandlingException;
import org.jboss.arquillian.persistence.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.data.script.ScriptHelper;
import org.jboss.arquillian.persistence.event.ApplyCleanupStatement;
import org.jboss.arquillian.persistence.event.ApplyInitStatement;
import org.jboss.arquillian.persistence.event.CleanupData;
import org.jboss.arquillian.persistence.event.CleanupDataUsingScript;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.ExecuteScripts;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.persistence.test.AssertionErrorCollector;

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
   public void cleanupStatements(@Observes ApplyCleanupStatement applyCleanupStatementEvent)
   {
      final String cleanupScript = applyCleanupStatementEvent.getCleanupStatement();
      if (cleanupScript == null || cleanupScript.isEmpty())
      {
         return;
      }
      executeScript(cleanupScript);
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
         throw new DBUnitDataSetHandlingException("Failed while seeding database.", e);
      }
   }

   @Override
   public void compare(@Observes CompareData compareDataEvent)
   {
      try
      {
         IDataSet currentDataSet = databaseConnection.get().createDataSet();
         IDataSet expectedDataSet = DataSetUtils.mergeDataSets(dataSetRegister.get().getExpected());
         new DataSetComparator().compare(currentDataSet, expectedDataSet, assertionErrorCollector.get());
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

   @Override
   public void cleanupUsingScript(@Observes CleanupDataUsingScript cleanupDataUsingScriptEvent)
   {
      for (SqlScriptResourceDescriptor scriptDescriptor : cleanupDataUsingScriptEvent.getDescriptors())
      {
         final String script = ScriptHelper.loadScript(scriptDescriptor.getLocation());
         executeScript(script);
      }
   }

   @Override
   public void executeScripts(@Observes ExecuteScripts executeScriptsEvent)
   {
      for (SqlScriptResourceDescriptor scriptDescriptor : executeScriptsEvent.getDescriptors())
      {
         final String script = ScriptHelper.loadScript(scriptDescriptor.getLocation());
         executeScript(script);
      }

   }

   // Private methods

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
         throw new DBUnitDataSetHandlingException("Unable to execute script: " + script, e);
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
               throw new DBUnitDataSetHandlingException("Unable to close statement after script execution.", e);
            }
         }
      }
   }

   private void fillDatabase() throws Exception
   {
      final DatabaseConnection connection = databaseConnection.get();
      IDataSet initialDataSet = DataSetUtils.mergeDataSets(dataSetRegister.get().getInitial());
      new TransactionOperation(DatabaseOperation.INSERT).execute(connection, initialDataSet);
   }

   private void cleanDatabase(CleanupStrategy cleanupStrategy)
   {
      final CleanupStrategyExecutor cleanupStrategyExecutor = new CleanupStrategyProvider(databaseConnection.get(), dataSetRegister.get()).create(cleanupStrategy);
      cleanupStrategyExecutor.cleanupDatabase();
   }

}
