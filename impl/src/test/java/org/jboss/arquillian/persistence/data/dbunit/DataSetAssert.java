package org.jboss.arquillian.persistence.data.dbunit;

import org.fest.assertions.Assertions;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.fest.assertions.GenericAssert;

public class DataSetAssert extends GenericAssert<DataSetAssert, IDataSet>
{

   protected DataSetAssert(IDataSet actual)
   {
      super(DataSetAssert.class, actual);
   }

   public DataSetAssert hasTables(String... tables)
   {
      try
      {
         Assertions.assertThat(actual.getTableNames()).contains(tables);
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
      
      return this;
   }

   public static DataSetAssert assertThat(IDataSet dataSet)
   {
      return new DataSetAssert(dataSet);
   }

}
