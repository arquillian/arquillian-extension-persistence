package org.jboss.arquillian.persistence.dbunit.dataset.json;


import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;

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
      assertEquals("DataType is not the expected one", DataType.INTEGER, converter.convertJSonDataTypeToDBUnitDataType(Integer.class));
   }

   @Test
   public void withNull()
   {
      assertEquals("DataType is not the expected one", DataType.VARCHAR, converter.convertJSonDataTypeToDBUnitDataType(null));
   }

   @Test
   public void unkownClass()
   {
      assertEquals("DataType is not the expected one", DataType.VARCHAR, converter.convertJSonDataTypeToDBUnitDataType(Before.class));
   }
}
