/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jboss.arquillian.persistence.exception.ConfigurationExportException;

public class ConfigurationExporter
{

   private static final String PROPERTY_PREFIX = "arquillian.extension.persistence.";

   private final PersistenceConfiguration persistenceConfiguration;

   public ConfigurationExporter(PersistenceConfiguration persistenceConfiguration)
   {
      this.persistenceConfiguration = persistenceConfiguration;
   }

   public void toProperties(final OutputStream output)
   {
      try
      {
         serializeFieldsToProperties(output);
      }
      catch (Exception e)
      {
         throw new ConfigurationExportException("Unable to serialize persistence configuration to property file.", e);
      }
      finally
      {
         if (output != null)
         {
            try
            {
               output.close();
            }
            catch (IOException e)
            {
               throw new ConfigurationExportException("Unable to close stream after serialization of persistence configuration to property file.", e);
            }
         }
      }


   }

   private void serializeFieldsToProperties(final OutputStream output)
         throws IOException, IllegalArgumentException, IllegalAccessException
   {
      final Map<String, String> fieldsWithValues = extractFieldsWithValues();
      for (Entry<String, String> entry : fieldsWithValues.entrySet())
      {
         output.write(serializeAsProperty(entry).getBytes());
      }
   }

   private String serializeAsProperty(Entry<String, String> entry)
   {
      String serializedAsProperty;
      final StringBuilder sb = new StringBuilder();
      sb.append(entry.getKey())
        .append("=")
        .append(entry.getValue())
        .append('\n');
      serializedAsProperty = sb.toString();
      return serializedAsProperty;
   }

   private Map<String, String> extractFieldsWithValues() throws IllegalArgumentException, IllegalAccessException
   {
      final Map<String,String> extractedValues = new HashMap<String, String>();
      List<Field> fields = SecurityActions.getAccessibleFields(persistenceConfiguration.getClass());

      for (Field field : fields)
      {
         Object object = field.get(persistenceConfiguration);
         String key = convertToPropertyKey(field.getName());
         if (object != null)
         {
            extractedValues.put(key, object.toString());
         }
      }

      return extractedValues;
   }

   private String convertToPropertyKey(String key)
   {
      final StringBuilder sb = new StringBuilder();
      sb.append(PROPERTY_PREFIX);

      for (int i = 0; i < key.length(); i++) {
          char c = key.charAt(i);
          if (Character.isUpperCase(c)) {
              sb.append('.').append(Character.toLowerCase(c));
          } else {
              sb.append(c);
          }
      }

      return sb.toString();
   }

}
