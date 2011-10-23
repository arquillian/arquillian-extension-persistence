package org.jboss.arquillian.persistence.data.dbunit.dataset.yaml;

import java.io.InputStream;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitInitializationException;

public class YamlDataSetBuilder extends DataSetBuilder
{

   @Override
   public IDataSet build(String file)
   {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
      IDataSet dataSet = null;
      try
      {
         dataSet = new YamlDataSet(inputStream);
      }
      catch (DataSetException e)
      {
         throw new DBUnitInitializationException("Unable to load data set from given file : " + file, e);
      }
      
      return dataSet;
   }

}
