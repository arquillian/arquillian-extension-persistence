package org.jboss.arquillian.persistence.dbunit.dataset.json;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;

public enum DatabaseDataType {
   ORACLESTRING(JsonDataType.STRING, Oracle10DataTypeFactory.class, DataType.VARCHAR),
   ORACLEBIGINTEGER(JsonDataType.BIGINTEGER, Oracle10DataTypeFactory.class, DataType.DECIMAL),
   ORACLEINTEGER(JsonDataType.INTEGER, Oracle10DataTypeFactory.class, DataType.DECIMAL),
   ORACLELONG(JsonDataType.LONG, Oracle10DataTypeFactory.class, DataType.DECIMAL),
   ORACLETIMESTAMP(JsonDataType.TIMESTAMP, Oracle10DataTypeFactory.class, DataType.TIMESTAMP),
   HSQLSTRING(JsonDataType.STRING, HsqldbDataTypeFactory.class, DataType.VARCHAR),
   HSQLBIGINTEGER(JsonDataType.BIGINTEGER, HsqldbDataTypeFactory.class, DataType.BIGINT),
   HSQLINTEGER(JsonDataType.INTEGER, HsqldbDataTypeFactory.class, DataType.BIGINT),
   HSQLLONG(JsonDataType.LONG, HsqldbDataTypeFactory.class, DataType.BIGINT),
   HSQLTIMESTAMP(JsonDataType.TIMESTAMP, HsqldbDataTypeFactory.class, DataType.TIMESTAMP);

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
