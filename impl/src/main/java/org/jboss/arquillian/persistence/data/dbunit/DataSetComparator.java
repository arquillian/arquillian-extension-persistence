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

import static org.jboss.arquillian.persistence.data.dbunit.DataSetUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedTable;
import org.jboss.arquillian.persistence.test.AssertionErrorCollector;

public class DataSetComparator
{

   private static final Logger log = Logger.getLogger(DataSetComparator.class.getName());

   final List<String> generalColumnsToExclude = new ArrayList<String>();

   final Map<String, List<String>> columnsPerTableToExclude = new HashMap<String, List<String>>();

   public DataSetComparator(String ... columnsToExclude)
   {
      mapColumsToExclude(columnsToExclude);
   }

   public void compare(IDataSet currentDataSet, IDataSet expectedDataSet, AssertionErrorCollector errorCollector) throws DatabaseUnitException
   {
      final String[] tableNames = expectedDataSet.getTableNames();
      for (String tableName : tableNames)
      {
         final SortedTable expectedTableState = new SortedTable(expectedDataSet.getTable(tableName));
         final SortedTable currentTableState = new SortedTable(currentDataSet.getTable(tableName),
               expectedTableState.getTableMetaData());
         final List<String> columnsToIgnore = extractColumnsToBeIgnored(expectedTableState, currentTableState);
         try
         {
            Assertion.assertEqualsIgnoreCols(expectedTableState, currentTableState,
                  columnsToIgnore.toArray(new String[columnsToIgnore.size()]));
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

   // -- Private methods

   private List<String> extractColumnsToBeIgnored(final SortedTable expectedTableState,
         final SortedTable currentTableState) throws DataSetException
   {
      final List<String> columnsToIgnore = extractColumnsNotSpecifiedInExpectedDataSet(expectedTableState,
            currentTableState);
      final String tableName = expectedTableState.getTableMetaData().getTableName();
      final List<String> tableColumns = columnsPerTableToExclude.get(tableName);
      if (tableColumns != null)
      {
         columnsToIgnore.addAll(tableColumns);
      }
      if (!generalColumnsToExclude.isEmpty())
      {
         columnsToIgnore.addAll(generalColumnsToExclude);
      }
      final List<String> nonExistingColumns = extractNonExistingColumns(columnsToIgnore, extractColumnNames(currentTableState.getTableMetaData().getColumns()));
      if (!nonExistingColumns.isEmpty())
      {
         log.warning("Columns which are specified to be filtered out [" +
               Arrays.toString(nonExistingColumns.toArray())+ "] are not existing in the table");
      }
      return columnsToIgnore;
   }

   private void mapColumsToExclude(String[] columnsToExclude)
   {
      for (String columnToExclude : columnsToExclude)
      {
         if (columnToExclude.length() == 0)
         {
            continue;
         }
         if (!columnToExclude.contains("."))
         {
            generalColumnsToExclude.add(columnToExclude);
         }
         else
         {
            splitTableAndColumn(columnToExclude);
         }
      }
   }


   private void splitTableAndColumn(String columnToExclude)
   {
      final String[] splittedTableAndColumn = columnToExclude.split("\\.");
      if (splittedTableAndColumn.length != 2)
      {
         throw new IllegalArgumentException("Cannot associated table with column for [" + columnToExclude
               + "]. Expected format: 'tableName.columnName'");
      }
      final String tableName = splittedTableAndColumn[0];
      List<String> tableColumns = columnsPerTableToExclude.get(tableName);
      if (tableColumns == null)
      {
         tableColumns = new ArrayList<String>();
         columnsPerTableToExclude.put(tableName, tableColumns);
      }

      tableColumns.add(splittedTableAndColumn[1]);
   }

}
