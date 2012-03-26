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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

public class TestConfigurationLoader
{

   private static final String DEFAULT_CONFIG_FILENAME = "arquillian.xml";

   public static InputStream loadArquillianConfiguration(String fileName)
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      return classLoader.getResourceAsStream(fileName);
   }

   public static ArquillianDescriptor createArquillianDescriptor(String fileName)
   {
      return Descriptors.importAs(ArquillianDescriptor.class).from(
            TestConfigurationLoader.loadArquillianConfiguration(fileName));
   }

   public static ArquillianDescriptor createArquillianDescriptorFromDefaultConfigurationFile()
   {
      return createArquillianDescriptor(DEFAULT_CONFIG_FILENAME);
   }

   public static PersistenceConfiguration createDefaultConfiguration()
   {
      return createPersistenceConfigurationFrom(DEFAULT_CONFIG_FILENAME);
   }

   public static Properties createPropertiesFromCustomConfigurationFile() throws IOException
   {
      Properties properties = new Properties();
      properties.load(loadArquillianConfiguration("properties/custom.arquillian.persistence.properties"));
      return properties;
   }

   public static PersistenceConfiguration createPersistenceConfigurationFrom(String fileName)
   {
      ArquillianDescriptor descriptor = createArquillianDescriptor(fileName);
      PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
      Configuration.importTo(persistenceConfiguration).loadFrom(descriptor);
      return persistenceConfiguration;
   }

}
