package org.jboss.arquillian.persistence.data.dbunit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fest.assertions.Assertions;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
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
