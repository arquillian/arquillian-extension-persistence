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
package org.jboss.arquillian.persistence.dbunit.dataset.json;


import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.regex.Pattern;

public class JsonDataTypeConverter {

   JsonDataTypeToDatabaseConverter jsonDataTypeToDatabaseConverter = new JsonDataTypeToDatabaseConverter();

   @Inject
   DBUnitConfiguration configuration;

   public DataType convertJSonDataTypeToDBUnitDataType(Object value, Class clazz)
   {
      DataType dataType = DataType.UNKNOWN;

      JsonDataType jsonDataType = dataTypeForClass(value, clazz);

      if (jsonDataType != JsonDataType.UNKNOWN)
      {
         dataType = jsonDataTypeToDatabaseConverter.dataTpeForJsonDataType(jsonDataType, configuration.getDatatypeFactory());
      }

      return dataType;
   }

   public JsonDataType dataTypeForClass(Object value, Class clazz)
   {
      JsonDataType jsonDataType = JsonDataType.STRING;

      for (JsonDataType currentDataType : JsonDataType.values())
      {
         boolean matches = match(value, currentDataType.getRegexPattern());

         if (currentDataType.getClazz().equals(clazz) && matches)
         {
            jsonDataType = currentDataType;
         }
      }

      return jsonDataType;
   }

   boolean match(Object value, String regexPattern)
   {
      boolean matches = false;

      if (null != regexPattern && null != value)
      {
         String stringValue = value.toString();
         matches = Pattern.matches(regexPattern, stringValue);
      }

      return matches;
   }
}
