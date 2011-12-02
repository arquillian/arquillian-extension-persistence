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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.persistence.exception.PersistenceExtensionInitializationException;

/**
 *
 * Fetches persistence-related configuration from <code>arquillian.xml</code> or
 * property file and creates {@see PersistenceConfiguration} instance used
 * during tests execution.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class ConfigurationImporter
{

   private static final String PERSISTENCE_EXTENSION_QUALIFIER = "persistence";

   private static final String PROPERTY_PREFIX = "arquillian.extension.persistence.";

   public ConfigurationImporter()
   {
   }

   public PersistenceConfiguration from(ArquillianDescriptor descriptor)
   {
      final Map<String, String> extensionProperties = extractPropertiesFromDescriptor(PERSISTENCE_EXTENSION_QUALIFIER, descriptor);
      return createPersistenceConfiguration(extensionProperties);
   }

   public PersistenceConfiguration from(Properties properties)
   {
      Map<String, String> fieldsWithValues = convertKeys(properties);
      return createPersistenceConfiguration(fieldsWithValues);
   }

   private Map<String, String> convertKeys(Properties properties)
   {
      Map<String, String> convertedFieldsWithValues = new HashMap<String, String>();
      for (Entry<Object, Object> property : properties.entrySet())
      {
         String key = (String) property.getKey();
         String value = (String) property.getValue();
         convertedFieldsWithValues.put(convertFromPropertyKey(key), value);

      }
      return convertedFieldsWithValues;
   }

   private String convertFromPropertyKey(String key)
   {
      key = key.replaceAll(PROPERTY_PREFIX, "");
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < key.length(); i++)
      {
         char c = key.charAt(i);
         if (c == '.')
         {
            c = Character.toUpperCase(key.charAt(++i));
         }
         sb.append(c);
      }
      return sb.toString();
   }

   private PersistenceConfiguration createPersistenceConfiguration(final Map<String, String> fieldsWithValues)
   {
      PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
      ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();
      List<Field> fields = SecurityActions.getAccessibleFields(PersistenceConfiguration.class);

      for (Field field : fields)
      {
         final String fieldName = field.getName();
         if (fieldsWithValues.containsKey(fieldName))
         {
            String value = fieldsWithValues.get(fieldName);
            Class<?> fieldType = field.getType();
            try
            {
               field.set(persistenceConfiguration, typeConverter.convert(value, typeConverter.box(fieldType)));
            }
            catch (Exception e)
            {
               throw new PersistenceExtensionInitializationException("Unable to create persistence configuration.", e);
            }
         }
      }

      return persistenceConfiguration;
   }

   private Map<String, String> extractPropertiesFromDescriptor(String extenstionName, ArquillianDescriptor descriptor)
   {
      for (ExtensionDef extension : descriptor.getExtensions())
      {
         if (extenstionName.equals(extension.getExtensionName()))
         {
            return extension.getExtensionProperties();
         }
      }

      return Collections.<String, String> emptyMap();
   }

}
