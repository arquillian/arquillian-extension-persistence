package org.jboss.arquillian.persistence.dbunit.dataset.json;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;

/**
 * Only define exceptions to the default value defined in <code>JsonDataType</code>.
 */
public enum DatabaseDataType {
   ORACLEBIGINTEGER(JsonDataType.BIGINTEGER, Oracle10DataTypeFactory.class, DataType.DECIMAL),
   ORACLEINTEGER(JsonDataType.INTEGER, Oracle10DataTypeFactory.class, DataType.DECIMAL),
   ORACLELONG(JsonDataType.LONG, Oracle10DataTypeFactory.class, DataType.DECIMAL),
   HSQLBIGINTEGER(JsonDataType.BIGINTEGER, HsqldbDataTypeFactory.class, DataType.BIGINT),
   HSQLINTEGER(JsonDataType.INTEGER, HsqldbDataTypeFactory.class, DataType.BIGINT),
   HSQLLONG(JsonDataType.LONG, HsqldbDataTypeFactory.class, DataType.BIGINT);

   private JsonDataType jsonDataType;

   private Class databaseFactoryClazz;

   private DataType dataType;

   private DatabaseDataType(JsonDataType jsonDataType, Class databaseFactoryClazz, DataType dataType)
   {
      this.jsonDataType = jsonDataType;
      this.databaseFactoryClazz = databaseFactoryClazz;
      this.dataType = dataType;
   }

   public JsonDataType getJsonDataType()
   {
      return jsonDataType;
   }

   public DataType getDataType()
   {
      return dataType;
   }

   public Class getDatabaseFactoryClass()
   {
      return databaseFactoryClazz;
   }
}
