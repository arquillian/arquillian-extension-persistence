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
package org.jboss.arquillian.persistence.data.dbunit.configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.arquillian.persistence.data.dbunit.configuration.annotations.Feature;
import org.jboss.arquillian.persistence.data.dbunit.configuration.annotations.Property;


public class DBUnitConfigurationPropertyMapper
{

   private static final String FEATURE_PREFIX = "http://www.dbunit.org/features/";

   private static final String PROPERTY_PREFIX = "http://www.dbunit.org/properties/";

   public Map<String, Object> map(DBUnitConfiguration configuration)
   {
      final Map<String, Object> convertedProperties = new HashMap<String, Object>();
      mapProperties(configuration, convertedProperties);
      mapFeatures(configuration, convertedProperties);
      return convertedProperties ;
   }

   private void mapFeatures(DBUnitConfiguration configuration, final Map<String, Object> convertedProperties)
   {
      final List<Field> features = ReflectionHelper.getFieldsWithAnnotation(DBUnitConfiguration.class, Feature.class);
      try
      {
         for (Field feature : features)
         {
            String featurePrefix = FEATURE_PREFIX;
            final Feature featureAnnotation = feature.getAnnotation(Feature.class);
            if (!featureAnnotation.value().isEmpty())
            {
               featurePrefix += featureAnnotation.value() + "/";
            }
            final String key = featurePrefix + feature.getName();
            final Object value = feature.get(configuration);
            if (value != null)
            {
               convertedProperties.put(key, value);
            }
         }
      }
      catch (Exception e)
      {
         // TODO introduce / reuse dbunit exception
         throw new RuntimeException("Unable to map dbunit settings", e);
      }
   }

   private void mapProperties(DBUnitConfiguration configuration, final Map<String, Object> convertedProperties)
   {
      final List<Field> properties = ReflectionHelper.getFieldsWithAnnotation(DBUnitConfiguration.class, Property.class);
      try
      {
         for (Field property : properties)
         {
            String propertyPrefix = PROPERTY_PREFIX;
            final Property propertyAnnotation = property.getAnnotation(Property.class);
            if (!propertyAnnotation.value().isEmpty())
            {
               propertyPrefix += propertyAnnotation.value() + "/";
            }
            final String key = propertyPrefix + property.getName();
            final Object value = property.get(configuration);
            if (value != null)
            {
               convertedProperties.put(key, value);
            }
         }
      }
      catch (Exception e)
      {
         // TODO introduce / reuse dbunit exception
         throw new RuntimeException("Unable to map dbunit settings", e);
      }
   }

}
