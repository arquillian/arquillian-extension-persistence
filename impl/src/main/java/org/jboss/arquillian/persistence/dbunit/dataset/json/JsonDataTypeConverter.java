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

import java.math.BigInteger;
import java.util.ArrayList;

public class JsonDataTypeConverter {

   public DataType convertJSonDataTypeToDBUnitDataType(Class clazz, boolean containsTimestampCharachters)
   {
      return JsonDataTypes.dataTypeForClass(clazz, containsTimestampCharachters);
   }

   public boolean containsTimestampCharacters(String value)
   {
      boolean constainsTimestampCharacters = false;

      // TODO REGEX PATTERN TO CHECK IF THE VALUE IS A TIMESTAMP

      return constainsTimestampCharacters;
   }

   private static enum JsonDataTypes {
      STRING(String.class, DataType.VARCHAR, false),
      INTEGER(Integer.class, DataType.DECIMAL, false),
      LONG(Long.class,DataType.DECIMAL, false),
      BIGINTEGER(BigInteger.class, DataType.DECIMAL, false),
      UNKNOWN(ArrayList.class, DataType.UNKNOWN, false), // value == [null], we have no data type information available
      TIMESTAMP(String.class, DataType.TIMESTAMP, true);

      private Class clazz;

      private DataType dataType;

      private boolean containsTimestampCharacters = false;

      private JsonDataTypes(Class clazz, DataType dataType, boolean containsTimestampCharacters)
      {
         this.clazz = clazz;
         this.dataType = dataType;
         this.containsTimestampCharacters = containsTimestampCharacters;
      }

      public static DataType dataTypeForClass(Class clazz, boolean containsTimestampCharacters)
      {
         DataType dataType = DataType.VARCHAR;

         for (JsonDataTypes currentDataType : JsonDataTypes.values())
         {
            if (currentDataType.clazz.equals(clazz) && currentDataType.containsTimestampCharacters == containsTimestampCharacters)
            {
               dataType = currentDataType.dataType;
            }
         }

         return dataType;
      }
   }
}
