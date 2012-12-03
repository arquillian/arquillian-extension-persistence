package org.jboss.arquillian.persistence.dbunit.dataset.json;


import org.dbunit.dataset.datatype.DataType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * The regex pattern differentiates between the same data types reported by Jackson and the real data type to be applied
 * for DBUnit so the comparison with the real data type read from the metadata of the database by DBUnit. Because we have
 * no data type information available in JSON, we have to perform a best guess. For example, a normal String and a Timestamp
 * are reported both as data type String and whould end up as a VARCHAR. The differentiation is done by an additional regex pattern
 * which will detect the real datatypes. The Json data type mapping to java is documented here
 * <url>http://wiki.fasterxml.com/JacksonInFiveMinutes</url>.
 */
public enum JsonDataType {
   STRING(String.class, "[^-|:]*", DataType.VARCHAR),
   INTEGER(Integer.class, "\\d*", DataType.INTEGER),
   LONG(Long.class, "\\d*", DataType.INTEGER),
   BIGINTEGER(BigInteger.class, "\\d*", DataType.INTEGER),
   DOUBLE(Double.class, "\\d*\\.\\d*", DataType.DOUBLE),
   BOOLEAN(Boolean.class, ".*", DataType.BOOLEAN),
   UNKNOWN(ArrayList.class, ".*", DataType.UNKNOWN), // value == [null], we have no data type information available
   TIMESTAMP(String.class, "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}[\\.\\d]*", DataType.TIMESTAMP);

   private Class clazz;

   private String regexPattern;

   private DataType defaultDataType;

   private JsonDataType(Class clazz, String regexPattern, DataType defaultDataType)
   {
      this.clazz = clazz;
      this.regexPattern = regexPattern;
      this.defaultDataType = defaultDataType;
   }

   public String getRegexPattern()
   {
      return regexPattern;
   }

   public Class getClazz()
   {
      return clazz;
   }

   public DataType getDefaultDataType()
   {
      return defaultDataType;
   }
}
