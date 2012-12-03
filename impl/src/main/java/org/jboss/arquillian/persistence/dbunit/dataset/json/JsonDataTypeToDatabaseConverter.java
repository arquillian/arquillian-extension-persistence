package org.jboss.arquillian.persistence.dbunit.dataset.json;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.util.logging.Logger;

public class JsonDataTypeToDatabaseConverter {

   public DataType dataTpeForJsonDataType(JsonDataType jsonDataType, IDataTypeFactory factory)
   {
      DataType dataType = jsonDataType.getDefaultDataType();

      for(DatabaseDataType currentDatabaseDataType : DatabaseDataType.values())
      {
         if (currentDatabaseDataType.getDatabaseFactoryClass().equals(factory.getClass()) &&
                 currentDatabaseDataType.getJsonDataType().equals(jsonDataType))
         {
            dataType = currentDatabaseDataType.getDataType();
         }
      }

      return dataType;
   }
}
