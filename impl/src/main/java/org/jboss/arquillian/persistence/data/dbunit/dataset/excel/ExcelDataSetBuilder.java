package org.jboss.arquillian.persistence.data.dbunit.dataset.excel;

import java.io.InputStream;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitInitializationException;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetBuilder;

public class ExcelDataSetBuilder extends DataSetBuilder
{

   @Override
   public IDataSet build(String file)
   {
      InputStream xlsStream = getClass().getClassLoader().getResourceAsStream(file);
      IDataSet dataSet = null;
      try
      {
         dataSet = new XlsDataSet(xlsStream);
      }
      catch (Exception e)
      {
         throw new DBUnitInitializationException("Unable to load data set from given file : " + file, e);
      }
      
      return dataSet;
   }

}
