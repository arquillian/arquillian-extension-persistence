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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;
import org.jboss.arquillian.persistence.data.dbunit.dataset.Row;
import org.yaml.snakeyaml.Yaml;

public class TableAssert extends GenericAssert<TableAssert, ITable>
{

   protected TableAssert(ITable actual)
   {
      super(TableAssert.class, actual);
   }

   public TableAssert hasColumns(String... expectedColumnNames)
   {
      List<String> columnNames = extractColumnNames();
      Assertions.assertThat(columnNames).contains(expectedColumnNames);
      return this;
   }

   public TableAssert hasRow(String... keyValuePairs)
   {
      
      @SuppressWarnings("unchecked")
      Row expectedRow = new Row((Map<String, String>) new Yaml().load(flatten(keyValuePairs)));
      
      List<Row> rows = extractRows();
      Assertions.assertThat(rows).contains(expectedRow);
      
      return this;
   }

   public TableAssert hasRows(int amount)
   {
      Assertions.assertThat(actual.getRowCount()).isEqualTo(amount);
      return this;
   }

   public static TableAssert assertThat(ITable table)
   {
      return new TableAssert(table);
   }

   private List<String> extractColumnNames()
   {
      List<String> columnNames = new ArrayList<String>();
      Column[] columns;
      try
      {
         columns = actual.getTableMetaData().getColumns();
         for (Column column : columns)
         {
            columnNames.add(column.getColumnName());
         }
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
      return columnNames;
   }

   private List<Row> extractRows()
   {
      final List<Row> extractedRows = new ArrayList<Row>();
      int rowCount = actual.getRowCount();
      final List<String> columnNames = extractColumnNames();
      try {
         for (int i = 0; i < rowCount; i++)
         {
            final Map<String, String> cells = extractRow(i, columnNames);
            extractedRows.add(new Row(cells));
         }
      }
      catch (DataSetException e) 
      {
         throw new RuntimeException(e);
      }
      return extractedRows;
   }

   private Map<String, String> extractRow(int rowIndex, final List<String> columnNames) throws DataSetException
   {
      final Map<String, String> cells = new HashMap<String, String>();
      for (String columnName : columnNames)
      {
         String value = (String) actual.getValue(rowIndex, columnName);
         if (value != null)
         {
            cells.put(columnName, value);
         }
      }
      return cells;
   }

   
   private String flatten(String... keyValuePairs)
   {
      StringBuilder flattenedString = new StringBuilder();
      for (String keyValue : keyValuePairs)
      {
         flattenedString.append(keyValue).append("\n");
      }
      return flattenedString.toString();
   }

}
