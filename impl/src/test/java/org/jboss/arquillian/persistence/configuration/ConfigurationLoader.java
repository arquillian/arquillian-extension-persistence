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
package org.jboss.arquillian.persistence.configuration;

import java.io.InputStream;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
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
