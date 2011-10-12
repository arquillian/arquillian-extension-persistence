package org.jboss.arquillian.persistence.util;

import java.io.InputStream;

public class TestConfigurationLoader
{

   private static final String CONFIG_FILENAME = "arquillian.xml";
   
   public static InputStream loadArquillianConfiguration()
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      return classLoader.getResourceAsStream(CONFIG_FILENAME);
   }
   
}
