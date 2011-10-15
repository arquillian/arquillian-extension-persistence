package org.jboss.arquillian.persistence.data.dbunit;

import java.util.ArrayList;
import java.util.List;

import org.fest.assertions.Assertions;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.fest.assertions.GenericAssert;

public class TableAssert extends GenericAssert<TableAssert, ITable>
{

   protected TableAssert(ITable actual)
   {
      super(TableAssert.class, actual);
   }

   public TableAssert hasColumns(String... expectedColumnNames)
   {
      try
      {
         List<String> columnNames = extractColumnNames();
         Assertions.assertThat(columnNames).contains(expectedColumnNames);
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
      
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
   
   private List<String> extractColumnNames() throws DataSetException
   {
      List<String> columnNames = new ArrayList<String>();
      Column[] columns = actual.getTableMetaData().getColumns();
      for (Column column : columns)
      {
         columnNames.add(column.getColumnName());
      }
      return columnNames;
   }

}
