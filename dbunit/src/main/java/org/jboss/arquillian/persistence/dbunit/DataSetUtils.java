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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.ExcludeTableFilter;
import org.jboss.arquillian.persistence.dbunit.api.CustomColumnFilter;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DataSetUtils
{

   public static IDataSet mergeDataSets(final List<IDataSet> dataSets) throws DataSetException
   {
      return new CompositeDataSet(dataSets.toArray(new IDataSet[dataSets.size()]));
   }

   public static List<String> extractColumnsNotSpecifiedInExpectedDataSet(final ITable expectedTableState, final ITable currentTableState) throws DataSetException
   {
      final Set<String> allColumns = new HashSet<String>(extractColumnNames(currentTableState.getTableMetaData().getColumns()));
      final Set<String> expectedColumnNames = new HashSet<String>(extractColumnNames(expectedTableState.getTableMetaData().getColumns()));
      return extractNonExistingColumns(allColumns, expectedColumnNames);
   }

   /**
    * Provides list of columns defined in expectedColumns, but not listed in actualColumns.
    *
    * @param expectedColumns
    * @param actualColumns
    * @return
    */
   public static List<String> extractNonExistingColumns(final Collection<String> expectedColumns,
         final Collection<String> actualColumns)
   {
      final List<String> columnsNotSpecifiedInExpectedDataSet = new ArrayList<String>();

      for (String column : expectedColumns)
      {
         if (!actualColumns.contains(column.toLowerCase()))
         {
            columnsNotSpecifiedInExpectedDataSet.add(column.toLowerCase());
         }
      }

      return columnsNotSpecifiedInExpectedDataSet;
   }

   public static Collection<String> extractColumnNames(final Column[] columns)
   {
      final List<String> names = new ArrayList<String>(columns.length);
      for (Column column : columns)
      {
         names.add(column.getColumnName().toLowerCase());
      }
      return names;
   }

   public static IDataSet excludeTables(IDataSet dataSet, String ... tablesToExclude)
   {
      return new FilteredDataSet(new ExcludeTableFilter(tablesToExclude), dataSet);
   }

   public static String[] tableNamesInUpperCase(String ... tableNamesOriginal) throws DataSetException
   {
      final List<String> tableNamesUpperCased = new ArrayList<String>(tableNamesOriginal.length);
      for (String tableName : tableNamesOriginal)
      {
         tableNamesUpperCased.add(tableName.toUpperCase());
      }
      return tableNamesUpperCased.toArray(new String[tableNamesUpperCased.size()]);
   }
}
