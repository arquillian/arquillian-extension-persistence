package org.jboss.arquillian.persistence.data.dbunit.dataset;

import org.dbunit.dataset.IDataSet;
import org.jboss.arquillian.persistence.Format;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitInitializationException;
import org.jboss.arquillian.persistence.data.dbunit.dataset.excel.ExcelDataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.dataset.xml.XmlDataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.dataset.yaml.YamlDataSetBuilder;

public abstract class DataSetBuilder
{

   public abstract IDataSet build(String file);

   public static DataSetBuilder builderFor(Format format)
   {
      switch (format)
      {
         case XML:
            return new XmlDataSetBuilder();
         case XLS:
            return new ExcelDataSetBuilder();
         case YAML:
            return new YamlDataSetBuilder();
         default:
            throw new DBUnitInitializationException("Unsupported data type " + format);
      }
   }
   
}
