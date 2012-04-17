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
package org.jboss.arquillian.persistence.core.configuration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.core.util.Strings;
import org.jboss.arquillian.persistence.dbunit.data.descriptor.Format;

/**
*
* @author <a href="kpiwko@redhat.com">Karel Piwko</a>
* @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
*
*/
class ConfigurationTypeConverter
{

   /**
    * A helper boxing method. Returns boxed class for a primitive class
    *
    * @param primitive A primitive class
    * @return Boxed class if class was primitive, unchanged class in other cases
    */
   public Class<?> box(Class<?> primitive)
   {
      if (!primitive.isPrimitive())
      {
         return primitive;
      }

      if (int.class.equals(primitive))
      {
         return Integer.class;
      }
      else if (long.class.equals(primitive))
      {
         return Long.class;
      }
      else if (float.class.equals(primitive))
      {
         return Float.class;
      }
      else if (double.class.equals(primitive))
      {
         return Double.class;
      }
      else if (short.class.equals(primitive))
      {
         return Short.class;
      }
      else if (boolean.class.equals(primitive))
      {
         return Boolean.class;
      }
      else if (char.class.equals(primitive))
      {
         return Character.class;
      }
      else if (byte.class.equals(primitive))
      {
         return Byte.class;
      }

      throw new IllegalArgumentException("Unknown primitive type " + primitive);
   }

   /**
    * A helper converting method.
    *
    * Converts string to a class of given type
    * @param value String value to be converted
    * @param to Type of desired value
    *
    * @param <T> Type of returned value
    * @return Value converted to a appropriate type
    */
   public <T> T convert(String value, Class<T> to)
   {
      if (Strings.isEmpty(value) && !(String.class.equals(to) || String[].class.equals(to)))
      {
         return null;
      }

      if (String.class.equals(to))
      {
         return to.cast(value);
      }
      else if (Integer.class.equals(to))
      {
         return to.cast(Integer.valueOf(value));
      }
      else if (Double.class.equals(to))
      {
         return to.cast(Double.valueOf(value));
      }
      else if (Long.class.equals(to))
      {
         return to.cast(Long.valueOf(value));
      }
      else if (Boolean.class.equals(to))
      {
         return to.cast(Boolean.valueOf(value));
      }
      else if (URL.class.equals(to))
      {
         try
         {
            return to.cast(new URI(value).toURL());
         }
         catch (MalformedURLException e)
         {
            throw new IllegalArgumentException("Unable to convert value " + value + " to URL", e);
         }
         catch (URISyntaxException e)
         {
            throw new IllegalArgumentException("Unable to convert value " + value + " to URL", e);
         }
      }
      else if (URI.class.equals(to))
      {
         try
         {
            return to.cast(new URI(value));
         }
         catch (URISyntaxException e)
         {
            throw new IllegalArgumentException("Unable to convert value " + value + " to URL", e);
         }
      }
      else
      {
         String trimmedValue = extractEnumName(value);
         if (to.isEnum())
         {
            @SuppressWarnings({"unchecked","rawtypes"})
            final T enumInstance = (T) Enum.valueOf((Class<Enum>) to, trimmedValue.toUpperCase());
            return enumInstance;
         }
         else if (String[].class.equals(to))
         {
            final String[] convertedArray = value.split(",");
            if (convertedArray.length == 0)
            {
               return to.cast(new String[0]);
            }

            trimElements(convertedArray);

            if (convertedArray.length == 1 && hasOnlyBlanks(convertedArray))
            {
               return to.cast(new String[0]);
            }

            return to.cast(convertedArray);
         }
         else // Try to create instance via reflection
         {
            try
            {
               Object instance = Class.forName(value).newInstance();
               return to.cast(instance);
            }
            catch (Exception e)
            {
               throw new IllegalArgumentException("Unable to convert value [" + value + "] to a class [" + to.getName() + "].", e);
            }
         }
      }

   }

   private String extractEnumName(final String value)
   {
      String trimmedValue = value.trim();
      final int lastDot = trimmedValue.lastIndexOf('.');
      final boolean potentiallyFullyQualifiedEnumName = lastDot > 0 && trimmedValue.length() > 1;
      if (potentiallyFullyQualifiedEnumName)
      {
         trimmedValue = trimmedValue.substring(lastDot + 1);
      }
      return trimmedValue;
   }

   private void trimElements(String[] convertedArray)
   {
      if (convertedArray == null)
      {
         return;
      }

      for (int i = 0; i < convertedArray.length; i++)
      {
         convertedArray[i] = convertedArray[i].trim();
      }
   }

   private boolean hasOnlyBlanks(String[] convertedArray)
   {
      boolean hasOnlyBlanks = true;
      for (String element : convertedArray)
      {
         if (element.trim().length() != 0)
         {
            return false;
         }
      }
      return hasOnlyBlanks;
   }

}
