package org.jboss.arquillian.persistence.configuration;

import java.util.Collections;
import java.util.Map;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;

class ConfigurationExtractor
{

   private final ArquillianDescriptor descriptor;
   
   ConfigurationExtractor(ArquillianDescriptor descriptor)
   {
      this.descriptor = descriptor;
   }

   PersistenceConfiguration extract()
   {
      final Map<String, String> extensionProperties = extractProperties("persistence");
      PersistenceConfiguration configuration = new PersistenceConfiguration();
      configuration.setDefaultDataSource(extensionProperties.get("defaultDataSource"));
      configuration.setInitStatement(extensionProperties.get("initStatement"));
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
