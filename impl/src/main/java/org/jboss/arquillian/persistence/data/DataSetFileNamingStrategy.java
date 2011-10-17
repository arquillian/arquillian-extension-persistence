package org.jboss.arquillian.persistence.data;

import java.lang.reflect.Method;


public class DataSetFileNamingStrategy
{

   private final Format format;

   public DataSetFileNamingStrategy(Format format)
   {
      this.format = format;
   }

   public String createFileName(Class<?> testClass, Method testMethod)
   {
      return testClass.getName() + "#" + testMethod.getName() + format.extension();
   }
   
   public String createFileName(Class<?> testClass)
   {
      return testClass.getName() + format.extension();
   }

}
