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

import static org.jboss.arquillian.persistence.dbunit.DataSetUtils.extractColumnNames;
import static org.jboss.arquillian.persistence.dbunit.DataSetUtils.extractColumnsNotSpecifiedInExpectedDataSet;
import static org.jboss.arquillian.persistence.dbunit.DataSetUtils.extractNonExistingColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.assertion.DiffCollectingFailureHandler;
import org.dbunit.assertion.Difference;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.filter.IncludeTableFilter;
import org.jboss.arquillian.persistence.core.test.AssertionErrorCollector;
import org.jboss.arquillian.persistence.dbunit.exception.DBUnitDataSetHandlingException;

public class DataSetComparator
{

   private static final String DIFF_ERROR = "%s | In row %d: expected value of %s \"%s\" but was \"%s\".";

   private static final Logger log = Logger.getLogger(DataSetComparator.class.getName());

   final ColumnsHolder toExclude;

   final ColumnsHolder orderBy;

   public DataSetComparator(final String[] orderBy, final String[] toExclude)
   {
      this.toExclude = new ColumnsHolder(toExclude);
      this.orderBy = new ColumnsHolder(orderBy);
   }

   public void compare(IDataSet currentDataSet, IDataSet expectedDataSet, AssertionErrorCollector errorCollector)
         throws DatabaseUnitException
   {
      if (expectedDataSet.getTableNames().length == 0)
      {
         shouldBeEmpty(currentDataSet, errorCollector);
      }
      else
      {
         compareContent(currentDataSet, expectedDataSet, errorCollector);
      }
   }

   public void compareContent(IDataSet currentDataSet, IDataSet expectedDataSet, AssertionErrorCollector errorCollector)
         throws DataSetException, DatabaseUnitException
   {
      final String[] tableNames = expectedDataSet.getTableNames();
      final FilteredDataSet filteredCurrentDataSet = new FilteredDataSet(new IncludeTableFilter(tableNames), currentDataSet);

      for (String tableName : tableNames)
      {
         final List<String> columnsForSorting = defineColumnsForSorting(filteredCurrentDataSet, expectedDataSet,
               tableName);

         final ITable expectedTable = sort(expectedDataSet, tableName, columnsForSorting);
         final ITable currentTable = sort(filteredCurrentDataSet, tableName, columnsForSorting);

         final List<String> columnsToIgnore = extractColumnsToBeIgnored(expectedDataSet.getTable(tableName),
               filteredCurrentDataSet.getTable(tableName));


         final DiffCollectingFailureHandler diffCollector = new DiffCollectingFailureHandler();

         Assertion.assertEquals(filter(expectedTable, toArray(columnsToIgnore)),
               filter(currentTable, toArray(columnsToIgnore)), diffCollector);

         @SuppressWarnings("unchecked")
         final List<Difference> diffs = diffCollector.getDiffList();
         collectErrors(errorCollector, diffs);
      }
   }

   public void shouldBeEmpty(IDataSet dataSet, AssertionErrorCollector errorCollector) throws DatabaseUnitException
   {
      final String[] tableNames = dataSet.getTableNames();
      for (String tableName : tableNames)
      {
         shouldBeEmpty(dataSet, tableName, errorCollector);
      }
   }

   public void shouldBeEmpty(IDataSet dataSet, String tableName, AssertionErrorCollector errorCollector)
         throws DataSetException
   {
      final SortedTable tableState = new SortedTable(dataSet.getTable(tableName));
      int rowCount = tableState.getRowCount();
      if (rowCount != 0)
      {
         errorCollector.collect(new AssertionError(tableName + " expected to be empty, but was <" + rowCount + ">."));
      }
   }

   // -- Private methods

   private void collectErrors(AssertionErrorCollector errorCollector, List<Difference> diffs)
   {
      for (Difference diff : diffs)
      {
         final String tableName = diff.getActualTable().getTableMetaData().getTableName();
         errorCollector.collect(String.format(DIFF_ERROR, tableName, diff.getRowIndex(), diff.getColumnName(),
               diff.getExpectedValue(), diff.getActualValue()));
      }
   }

   private ITable sort(IDataSet dataSet, String tableName, final List<String> columnsForSorting) throws DataSetException
   {
      final SortedTable sortedTable = new SortedTable(dataSet.getTable(tableName), toArray(columnsForSorting));
      sortedTable.setUseComparable(true);
      return sortedTable;
   }

   private List<String> defineColumnsForSorting(IDataSet currentDataSet, IDataSet expectedDataSet, String tableName)
         throws DataSetException
   {
      final List<String> columnsForSorting = new ArrayList<String>();
      columnsForSorting.addAll(orderBy.global);
      final List<String> columsPerTable = orderBy.columnsPerTable.get(tableName);
      if (columsPerTable != null)
      {
         columnsForSorting.addAll(columsPerTable);
      }
      columnsForSorting.addAll(additionalColumnsForSorting(expectedDataSet.getTable(tableName),
            currentDataSet.getTable(tableName)));
      return columnsForSorting;
   }

   private static <T> String[] toArray(final List<T> list)
   {
      return list.toArray(new String[list.size()]);
   }

   private List<String> additionalColumnsForSorting(final ITable expectedTableState, final ITable currentTableState)
   {
      final List<String> columnsForSorting = new ArrayList<String>();
      try
      {
         final Set<String> allColumns = new HashSet<String>(extractColumnNames(expectedTableState.getTableMetaData()
               .getColumns()));
         final Set<String> columnsToIgnore = new HashSet<String>(extractColumnsToBeIgnored(expectedTableState,
               currentTableState));
         for (String column : allColumns)
         {
            if (!columnsToIgnore.contains(column))
            {
               columnsForSorting.add(column);
            }
         }

      }
      catch (DataSetException e)
      {
         throw new DBUnitDataSetHandlingException("Unable to resolve columns in table " +
                 expectedTableState.getTableMetaData().getTableName(), e);
      }

      return columnsForSorting;
   }

   private List<String> extractColumnsToBeIgnored(final ITable expectedTableState, final ITable currentTableState)
         throws DataSetException
   {
      final List<String> columnsToIgnore = extractColumnsNotSpecifiedInExpectedDataSet(expectedTableState,
            currentTableState);
      final String tableName = expectedTableState.getTableMetaData().getTableName();
      final List<String> tableColumns = toExclude.columnsPerTable.get(tableName);

      columnsToIgnore.addAll(toExclude.global);

      if (tableColumns != null)
      {
         columnsToIgnore.addAll(tableColumns);
      }

      final List<String> nonExistingColumns = extractNonExistingColumns(columnsToIgnore,
            extractColumnNames(currentTableState.getTableMetaData().getColumns()));

      if (!nonExistingColumns.isEmpty())
      {
         log.warning("Columns which are specified to be filtered out [" + Arrays.toString(nonExistingColumns.toArray())
                 + "] are not existing in the table " + expectedTableState.getTableMetaData().getTableName());
      }
      return columnsToIgnore;
   }

   private ITable filter(ITable table, String[] columnsToFilter) throws DataSetException
   {
      return DefaultColumnFilter.excludedColumnsTable(table, columnsToFilter);
   }

}
