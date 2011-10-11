package org.jboss.arquillian.persistence.data.dbunit;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.DataHandler;
import org.jboss.arquillian.persistence.event.CleanUpDataEvent;
import org.jboss.arquillian.persistence.event.PrepareDataEvent;
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
   Instance<DatabaseConnection> databaseConnectionInstance;
   
   @Inject @TestScoped
   Instance<IDataSet> dataSetInstance;
   
   @Inject @SuiteScoped
   Instance<PersistenceConfiguration> configuration;
   
   @Override
   public void prepare(@Observes(precedence = 1) PrepareDataEvent prepareDataEvent)
   {
      try
      {
         applyInitStatement();
         fillDatabase(prepareDataEvent.getSourceFile());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }

   @Override
   public void cleanup(@Observes CleanUpDataEvent cleanupDataEvent)
   {
      try
      {
         cleanDatabase();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
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
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void fillDatabase(String file) throws IOException, SQLException, DatabaseUnitException
   {
      DatabaseOperation.INSERT.execute(databaseConnectionInstance.get(), dataSetInstance.get());
   }

   private void cleanDatabase() throws DatabaseUnitException, SQLException
   {
      DatabaseConnection connection = databaseConnectionInstance.get();
      DatabaseOperation.DELETE_ALL.execute(connection, connection.createDataSet());
   }

}
