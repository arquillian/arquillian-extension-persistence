/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.persistence.client;

import java.util.Collections;
import java.util.Map;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.Format;

/**
 * 
 * Fetches persistence-related configuration from <code>arquillian.xml</code>
 * and creates {@see PersistenceConfiguration} instance used during tests
 * execution.
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
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
      
      configuration.setDumpData(Boolean.parseBoolean(extensionProperties.get("dumpData")));
      String dumpDirectory = extensionProperties.get("dumpDirectory");
      if (dumpDirectory != null)
      {
         configuration.setDumpDirectory(dumpDirectory);
      }

      String userTransactionJndi = extensionProperties.get("userTransactionJndi");
      if (userTransactionJndi != null)
      {
         configuration.setUserTransactionJndi(userTransactionJndi);
      }
      
      return configuration;
   }

   private Map<String, String> extractProperties(String extenstionName)
   {
      for (ExtensionDef extension : descriptor.getExtensions())
      {
         if (extenstionName.equals(extension.getExtensionName()))
         {
            return extension.getExtensionProperties();
         }
      }

      return Collections.<String, String>emptyMap();
   }

}
