package org.jboss.arquillian.persistence.data;

import java.lang.reflect.Method;

import org.jboss.arquillian.persistence.Format;

public class DefaultFileNamingStrategy
{

   private final Format format;

   public DefaultFileNamingStrategy(Format format)
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
