package org.jboss.arquillian.persistence.configuration;

import java.io.InputStream;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

public class ConfigurationLoader
{

   private static final String DEFAULT_CONFIG_FILENAME = "arquillian.xml";

   public static InputStream loadArquillianConfiguration(String fileName)
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      return classLoader.getResourceAsStream(fileName);
   }

   public static ConfigurationExtractor createConfigurationExtractor(String fileName)
   {
      ArquillianDescriptor descriptor = Descriptors.importAs(ArquillianDescriptor.class).from(
            ConfigurationLoader.loadArquillianConfiguration(fileName));

      ConfigurationExtractor configurationExtractor = new ConfigurationExtractor(descriptor);
      return configurationExtractor;
   }

   public static ConfigurationExtractor createConfigurationExtractorForDefaultConfiguration()
   {
      return createConfigurationExtractor(DEFAULT_CONFIG_FILENAME);
   }

   public static PersistenceConfiguration createDefaultConfiguration()
   {
      return createConfiguration(DEFAULT_CONFIG_FILENAME);
   }
   
   public static PersistenceConfiguration createConfiguration(String fileName)
   {
      ConfigurationExtractor configurationExtractor = createConfigurationExtractor(fileName);
      return configurationExtractor.extract();
   }

}
