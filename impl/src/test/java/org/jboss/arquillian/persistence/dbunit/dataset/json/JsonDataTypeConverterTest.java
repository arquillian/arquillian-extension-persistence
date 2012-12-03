package org.jboss.arquillian.persistence.dbunit.dataset.json;


import org.dbunit.dataset.datatype.DataType;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
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
      converter.configuration = new DBUnitConfiguration();
      converter.configuration.setDatatypeFactory(new HsqldbDataTypeFactory());
      converter.jsonDataTypeToDatabaseConverter = new JsonDataTypeToDatabaseConverter();
   }

   @Test
   public void withInteger()
   {
      assertEquals("DataType is not the expected one", DataType.BIGINT, converter.convertJSonDataTypeToDBUnitDataType(Integer.valueOf(1), Integer.class));
   }

   @Test
   public void withDouble()
   {
      assertEquals("DataType is not the expected one", DataType.DOUBLE, converter.convertJSonDataTypeToDBUnitDataType(Double.valueOf(12.345345), Double.class));
   }

   @Test
   public void withBoolean()
   {
      assertEquals("DataType is not the expected one", DataType.BOOLEAN, converter.convertJSonDataTypeToDBUnitDataType("true", Boolean.class));
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

   @Test
   public void withTimestampMilliseconds()
   {
      // A Timestamp is also reported as data type String by Jackson. We have to differenciate normal Strings and Timestamps
      assertEquals("Datatype is not the expected one", DataType.TIMESTAMP, converter.convertJSonDataTypeToDBUnitDataType("2012-12-12 00:00:00.425", String.class));
   }
}
