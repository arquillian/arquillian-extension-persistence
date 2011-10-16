package org.jboss.arquillian.persistence.data.dbunit;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.DataHandler;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.data.exception.DBUnitDataSetHandlingException;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

/**
 * 
 * @author Bartosz Majsak
 *
 */
public class DBUnitDatasetHandler implements DataHandler
{

   @Inject @TestScoped
   private Instance<DatabaseConnection> databaseConnectionInstance;
   
   @Inject @TestScoped
   private Instance<DataSetRegister> dataSetRegisterInstance;
   
   @Inject @SuiteScoped
   private Instance<PersistenceConfiguration> configuration;
   
   @Override
   public void prepare(@Observes(precedence = 1) PrepareData prepareDataEvent)
   {
      try
      {
         applyInitStatement();
         fillDatabase(prepareDataEvent.getSourceFile());
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }

   }

   @Override
   public void compare(@Observes CompareData compareDataEvent)
   {
      try
      {
         IDataSet currentDataSet = databaseConnectionInstance.get().createDataSet();
         IDataSet expectedDataSet = dataSetRegisterInstance.get().getExpected();
         String[] tableNames = expectedDataSet.getTableNames();
         for (String tableName : tableNames)
         {
            ITable currentTableState = currentDataSet.getTable(tableName);
            ITable expectedTableState = expectedDataSet.getTable(tableName);
            Assertion.assertEquals(expectedTableState, currentTableState);
         }
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }
   }
   
   @Override
   public void cleanup(@Observes CleanUpData cleanupDataEvent)
   {
      try
      {
         cleanDatabase();
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }
   }

   private void applyInitStatement()
   {
      PersistenceConfiguration persistenceConfiguration = configuration.get();
      if (!persistenceConfiguration.isInitStatementDefined())
      {
         return;
      }
      
      try
      {
         Statement initStatement = databaseConnectionInstance.get().getConnection().createStatement();
         initStatement.execute(persistenceConfiguration.getInitStatement());
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException(e);
      }
   }

   private void fillDatabase(String file) throws IOException, SQLException, DatabaseUnitException
   {
      DatabaseOperation.INSERT.execute(databaseConnectionInstance.get(), dataSetRegisterInstance.get().getInitial());
   }

   private void cleanDatabase() throws DatabaseUnitException, SQLException
   {
      DatabaseConnection connection = databaseConnectionInstance.get();
      DatabaseOperation.DELETE_ALL.execute(connection, connection.createDataSet());
   }

}
