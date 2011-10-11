package org.jboss.arquillian.persistence.data.dbunit;

import java.net.URL;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.event.PrepareDataEvent;
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
   private InstanceProducer<IDataSet> dataSetProducer;
   
   public void initialize(@Observes(precedence = 2) PrepareDataEvent prepareDataEvent)
   {
      createDatabaseConnection();
      createDataSet(prepareDataEvent.getSourceFile());
   }

   private void createDatabaseConnection()
   {
      try
      {
         DataSource dataSource = databaseSourceInstance.get();
         DatabaseConnection databaseConnection = new DatabaseConnection(dataSource.getConnection());
         databaseConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new DefaultDataTypeFactory());
         databaseConnectionProducer.set(databaseConnection);
      }
      catch (Exception e)
      {
         throw new DBUnitInitilaztionException("Unable to initialize database connection for dbunit module", e);
      }
   }


   private void createDataSet(String file)
   {
      URL xmlUrl = getClass().getClassLoader().getResource(file);
      FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
      try
      {
         // TODO dynamic dispatch based on file type
         FlatXmlDataSet fx = flatXmlDataSetBuilder.build(xmlUrl);
         dataSetProducer.set(fx);
      }
      catch (DataSetException e)
      {
         throw new DBUnitInitilaztionException("Unable to initialize data set for dbunit module", e);
      }
   }

}
