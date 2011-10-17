package org.jboss.arquillian.persistence.configuration;

import java.util.Collections;
import java.util.Map;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.persistence.Format;
import org.jboss.arquillian.persistence.TransactionMode;

class ConfigurationExtractor
{

   private static final String PERSISTENCE_EXTENSION_QUALIFIER = "persistence";
   
   private final ArquillianDescriptor descriptor;
   
   ConfigurationExtractor(ArquillianDescriptor descriptor)
   {
      this.descriptor = descriptor;
   }

   PersistenceConfiguration extract()
   {
      final Map<String, String> extensionProperties = extractProperties(PERSISTENCE_EXTENSION_QUALIFIER);
      final PersistenceConfiguration configuration = new PersistenceConfiguration();
      configuration.setDefaultDataSource(extensionProperties.get("defaultDataSource"));
      configuration.setInitStatement(extensionProperties.get("initStatement"));
      String defaultDataSetFormat = extensionProperties.get("defaultDataSetFormat");
      if (defaultDataSetFormat != null)
      {
         configuration.setDefaultDataSetFormat(Format.valueOf(defaultDataSetFormat.toUpperCase()));
      }
      String defaultTransactionMode = extensionProperties.get("defaultTransactionMode");
      if (defaultTransactionMode != null)
      {
         configuration.setDefaultTransactionMode(TransactionMode.valueOf(defaultTransactionMode.toUpperCase()));
      }
      return configuration;
   }

   private Map<String, String> extractProperties(String extenstionName)
   {
      Map<String, String> properties = Collections.emptyMap();

      for (ExtensionDef extension : descriptor.getExtensions())
      {
         if (extenstionName.equals(extension.getExtensionName()))
         {
            return extension.getExtensionProperties();
         }
      }

      return properties;
   }

}
