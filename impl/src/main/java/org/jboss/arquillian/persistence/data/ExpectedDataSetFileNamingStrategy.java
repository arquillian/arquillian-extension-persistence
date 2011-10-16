package org.jboss.arquillian.persistence.data;

import java.lang.reflect.Method;

import org.jboss.arquillian.persistence.Format;

public class ExpectedDataSetFileNamingStrategy
{

   private final DataSetFileNamingStrategy dataSetFileNamingStrategy;
   
   public ExpectedDataSetFileNamingStrategy(Format format)
   {
      this.dataSetFileNamingStrategy = new DataSetFileNamingStrategy(format);
   }

   public String createFileName(Class<?> testClass, Method testMethod)
   {
      return "expected-" + dataSetFileNamingStrategy.createFileName(testClass, testMethod);
   }
   
   public String createFileName(Class<?> testClass)
   {
      return "expected-" + dataSetFileNamingStrategy.createFileName(testClass);
   }

}
