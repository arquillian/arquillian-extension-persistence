package org.jboss.arquillian.persistence.data.dbunit.dataset;

import org.dbunit.dataset.IDataSet;
import org.jboss.arquillian.persistence.data.Format;
import org.jboss.arquillian.persistence.data.dbunit.dataset.excel.ExcelDataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.dataset.xml.XmlDataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.dataset.yaml.YamlDataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitInitializationException;

public abstract class DataSetBuilder
{

   public abstract IDataSet build(String file);

   public static DataSetBuilder builderFor(Format format)
   {
      switch (format)
      {
         case XML:
            return new XmlDataSetBuilder();
         case EXCEL:
            return new ExcelDataSetBuilder();
         case YAML:
            return new YamlDataSetBuilder();
         default:
            throw new DBUnitInitializationException("Unsupported data type " + format);
      }
   }
   
}
