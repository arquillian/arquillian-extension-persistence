package org.jboss.arquillian.persistence.dbunit.dataset.json;


import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class JsonDataTypeConverterTest {

   private JsonDataTypeConverter converter = null;

   @Before
   public void setup()
   {
      converter = new JsonDataTypeConverter();
   }

   @Test
   public void withInteger()
   {
      assertEquals("DataType is not the expected one", DataType.DECIMAL, converter.convertJSonDataTypeToDBUnitDataType(Integer.valueOf(1), Integer.class));
   }

   @Test
   public void withString()
   {
      assertEquals("DataType is not the expected one", DataType.VARCHAR, converter.convertJSonDataTypeToDBUnitDataType("Good morning", String.class));
   }
   @Test
   public void withNull()
   {
      assertEquals("DataType is not the expected one", DataType.VARCHAR, converter.convertJSonDataTypeToDBUnitDataType(null, null));
   }

   @Test
   public void unkownClass()
   {
      assertEquals("DataType is not the expected one", DataType.VARCHAR, converter.convertJSonDataTypeToDBUnitDataType("Fred Unkown", Before.class));
   }

   @Test
   public void specialValueLikeDBUnitNull()
   {
      // DBUnit placeholder [null] creates an empty ArrayList when parsed by Jackson. In order to compare against the real/correct datatype
      // from the database, we have to set it to unkown in order to prevent DBUnit to fail because the data types are not the same.
      ArrayList list =  new ArrayList();
      assertEquals("DataType is not the expected one", DataType.UNKNOWN, converter.convertJSonDataTypeToDBUnitDataType(list, ArrayList.class));
   }

   @Test
   public void withTimestamp()
   {
      // A Timestamp is also reported as data type String by Jackson. We have to differenciate normal Strings and Timestamps
      assertEquals("Datatype is not the expected one", DataType.TIMESTAMP, converter.convertJSonDataTypeToDBUnitDataType("2012-12-12 00:00:00", String.class));
   }
}
