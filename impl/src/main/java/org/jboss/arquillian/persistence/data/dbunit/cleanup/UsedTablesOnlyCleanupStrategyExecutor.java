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
package org.jboss.arquillian.persistence.data.dbunit.cleanup;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.jboss.arquillian.persistence.data.dbunit.DataSetUtils;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitDataSetHandlingException;

public class UsedTablesOnlyCleanupStrategyExecutor implements CleanupStrategyExecutor
{

   private final DatabaseConnection connection;

   private final DataSetRegister dataSetRegister;

   public UsedTablesOnlyCleanupStrategyExecutor(DatabaseConnection connection, DataSetRegister dataSetRegister)
   {
      this.connection = connection;
      this.dataSetRegister = dataSetRegister;
   }

   @Override
   public void cleanupDatabase(String ... tablesToExclude)
   {
      try
      {
         final IDataSet mergeDataSets = DataSetUtils.mergeDataSets(dataSetRegister.getInitial());
         final IDataSet dataSet = DataSetUtils.excludeTables(mergeDataSets, tablesToExclude);
         new TransactionOperation(DatabaseOperation.DELETE_ALL).execute(connection, dataSet);
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException("Unable to clean database.", e);
      }
   }
}
