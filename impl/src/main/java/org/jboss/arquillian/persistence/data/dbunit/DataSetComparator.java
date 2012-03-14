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

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedTable;
import org.jboss.arquillian.persistence.test.AssertionErrorCollector;

public class DataSetComparator
{

   public void compare(IDataSet currentDataSet, IDataSet expectedDataSet, AssertionErrorCollector errorCollector) throws DatabaseUnitException
   {
      final String[] tableNames = expectedDataSet.getTableNames();
      for (String tableName : tableNames)
      {
         final SortedTable expectedTableState = new SortedTable(expectedDataSet.getTable(tableName));
         final SortedTable currentTableState = new SortedTable(currentDataSet.getTable(tableName),
               expectedTableState.getTableMetaData());
         final String[] columnsToIgnore = DataSetUtils.columnsNotSpecifiedInExpectedDataSet(expectedTableState,
               currentTableState);
         try
         {
            Assertion.assertEqualsIgnoreCols(expectedTableState, currentTableState, columnsToIgnore);
         }
         catch (AssertionError error)
         {
            errorCollector.collect(error);
         }
      }
   }

   public void shouldBeEmpty(IDataSet dataSet, AssertionErrorCollector errorCollector) throws DatabaseUnitException
   {
      final String[] tableNames = dataSet.getTableNames();
      for (String tableName : tableNames)
      {
         shouldBeEmpty(tableName, dataSet, errorCollector);
      }
   }

   public void shouldBeEmpty(String tableName, IDataSet dataSet, AssertionErrorCollector errorCollector)
         throws DataSetException
   {
      final SortedTable tableState = new SortedTable(dataSet.getTable(tableName));
      int rowCount = tableState.getRowCount();
      if (rowCount != 0)
      {
         errorCollector.collect(new AssertionError(tableName + "expected to be empty, but was <" + rowCount + ">."));
      }
   }

}
