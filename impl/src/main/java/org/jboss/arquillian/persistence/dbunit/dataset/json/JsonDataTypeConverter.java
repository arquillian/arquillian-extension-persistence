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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonDataTypeConverter {

   public DataType convertJSonDataTypeToDBUnitDataType(Object value, Class clazz)
   {
      return JsonDataTypes.dataTypeForClass(value, clazz);
   }

   /**
    * The regex pattern differentiates between the same data types reported by Jackson and the real data type to be applied
    * for DBUnit so the comparison with the real data type read from the metadata of the database by DBUnit. Because we have
    * no data type information available in JSON, we have to perform a best guess. For example, a normal String and a Timestamp
    * are reported both as data type String and whould end up as a VARCHAR. The differentiation is done by an additional regex pattern
    * which will detect the real datatypes.
    */
   private static enum JsonDataTypes {
      STRING(String.class, DataType.VARCHAR, "[^-|:]*"),
      INTEGER(Integer.class, DataType.DECIMAL, "\\d*"),
      LONG(Long.class,DataType.DECIMAL, "\\d*"),
      BIGINTEGER(BigInteger.class, DataType.DECIMAL, "\\d*"),
      UNKNOWN(ArrayList.class, DataType.UNKNOWN, ".*"), // value == [null], we have no data type information available
      TIMESTAMP(String.class, DataType.TIMESTAMP, "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}[\\.\\d]*");

      private Class clazz;

      private DataType dataType;

      private String regexPattern;

      private JsonDataTypes(Class clazz, DataType dataType, String regexPattern)
      {
         this.clazz = clazz;
         this.dataType = dataType;
         this.regexPattern = regexPattern;
      }

      public static DataType dataTypeForClass(Object value, Class clazz)
      {
         DataType dataType = DataType.VARCHAR;

         for (JsonDataTypes currentDataType : JsonDataTypes.values())
         {
            boolean matches = match(value, currentDataType.regexPattern);

            if (currentDataType.clazz.equals(clazz) && matches)
            {
               dataType = currentDataType.dataType;
            }
         }

         return dataType;
      }

      public static boolean match(Object value, String regexPattern)
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
}
