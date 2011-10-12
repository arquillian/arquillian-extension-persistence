package org.jboss.arquillian.persistence.data.dbunit.dataset;

import java.net.URL;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitInitializationException;

public class XmlDataSetBuilder extends DataSetBuilder
{

   @Override
   public IDataSet build(String file)
   {
      URL fileLocation = getClass().getClassLoader().getResource(file);
      FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
      IDataSet dataSet = null;
      try
      {
         dataSet = flatXmlDataSetBuilder.build(fileLocation);
      }
      catch (DataSetException e)
      {
         throw new DBUnitInitializationException("Unable to load data set from given file : " + file, e);
      }
      
      return dataSet;
   }

}
