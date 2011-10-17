package org.jboss.arquillian.persistence.data.dbunit;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.data.Format;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

/**
 * 
 * @author Bartosz Majsak
 *
 */
public class DBUnitInitializer
{

   @Inject @TestScoped
   private Instance<DataSource> databaseSourceInstance;

   @Inject @TestScoped
   private InstanceProducer<DatabaseConnection> databaseConnectionProducer;

   @Inject @TestScoped
   private InstanceProducer<DataSetRegister> dataSetRegisterProducer;
   
   public void initializeDataSeeding(@Observes(precedence = 2) PrepareData prepareDataEvent)
   {
      createDatabaseConnection();
      createInitialDataSet(prepareDataEvent.getSourceFile(), prepareDataEvent.getFormat());
   }
   
   public void initializeDataVerification(@Observes(precedence = 2) CompareData compareDataEvent)
   {
      createExpectedDataSet(compareDataEvent.getSourceFile(), compareDataEvent.getFormat());
   }

   private void createDatabaseConnection()
   {
      try
      {
         DataSource dataSource = databaseSourceInstance.get();
         DatabaseConnection databaseConnection = new DatabaseConnection(dataSource.getConnection());
         databaseConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
               new DefaultDataTypeFactory());
         databaseConnectionProducer.set(databaseConnection);
      }
      catch (Exception e)
      {
         throw new DBUnitInitializationException("Unable to initialize database connection for dbunit module", e);
      }
   }

   private void createInitialDataSet(String file, Format format)
   {
      IDataSet initial = DataSetBuilder.builderFor(format).build(file);
      DataSetRegister dataSetRegister = new DataSetRegister();
      dataSetRegister.addInitial(initial);
      dataSetRegisterProducer.set(dataSetRegister);
   }
   
   private void createExpectedDataSet(String file, Format format)
   {
      IDataSet expected = DataSetBuilder.builderFor(format).build(file);
      DataSetRegister dataSetRegister = dataSetRegisterProducer.get();
      dataSetRegister.addExpected(expected);
   }

}
