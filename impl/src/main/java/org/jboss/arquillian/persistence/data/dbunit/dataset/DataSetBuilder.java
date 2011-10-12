package org.jboss.arquillian.persistence.data.dbunit.dataset;

import org.dbunit.dataset.IDataSet;
import org.jboss.arquillian.persistence.SourceType;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitInitializationException;

public abstract class DataSetBuilder
{

   public abstract IDataSet build(String file);

   public static DataSetBuilder from(SourceType type)
   {
      switch (type)
      {
         case XML:
            return new XmlDataSetBuilder();
         case XLS:
            return new ExcelDataSetBuilder();
         default:
            throw new DBUnitInitializationException("Unsupported data type " + type);
      }
   }
   
}
