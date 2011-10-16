package org.jboss.arquillian.persistence;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.persistence.event.TransactionFinished;
import org.jboss.arquillian.persistence.exception.DataSourceNotFoundException;
import org.jboss.arquillian.persistence.metadata.MetadataProvider;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;

/**
 * 
 * @author bmajsak
 *
 */
public class PersistenceTestHandler
{

   @Inject @SuiteScoped
   private Instance<PersistenceConfiguration> configuration;
   
   @Inject @TestScoped
   private InstanceProducer<javax.sql.DataSource> dataSourceProducer;
   
   @Inject @TestScoped
   private Event<PrepareData> prepareDataEvent;
   
   @Inject @TestScoped
   private Event<CompareData> compareDataEvent;
   
   @Inject @TestScoped
   private Event<CleanUpData> cleanUpDataEvent;
   
   @Inject @TestScoped
   private Event<TransactionFinished> transactionFinishedEvent;
   
   public void beforeTest(@Observes Before beforeTestEvent)
   {
      MetadataProvider metadataProvider = new MetadataProvider(beforeTestEvent, configuration.get());
      if (!metadataProvider.isPersistenceFeatureEnabled())
      {
         return;
      }

      String dataSourceName = metadataProvider.getDataSourceName();
      dataSourceProducer.set(loadDataSource(dataSourceName));
      
      prepareDataEvent.fire(new PrepareData(metadataProvider.getDataFileName(), metadataProvider.getDataFormat()));
   }

   public void afterTest(@Observes After afterTestEvent)
   {
      MetadataProvider metadataProvider = new MetadataProvider(afterTestEvent, configuration.get());
      if (!metadataProvider.isPersistenceFeatureEnabled())
      {
         return;
      }
      
      if (metadataProvider.isTransactional())
      {
         transactionFinishedEvent.fire(new TransactionFinished(afterTestEvent));
      }

      if (metadataProvider.isDataVerificationExpected())
      {
         String expectedDataFileName = metadataProvider.getExpectedDataFileName();
         Format expectedDataFormat = metadataProvider.getExpectedDataFormat();
         compareDataEvent.fire(new CompareData(expectedDataFileName, expectedDataFormat));
      }
      
      if (!metadataProvider.isTransactional() || TransactionMode.ROLLBACK.equals(metadataProvider.getTransactionalMode()))
      {
      }

      cleanUpDataEvent.fire(new CleanUpData());
      
   }

   // Private methods
   
   private DataSource loadDataSource(String dataSourceName)
   {
      try
      {
         final InitialContext context = new InitialContext();
         return (javax.sql.DataSource) context.lookup(dataSourceName);
      }
      catch (NamingException e)
      {
         throw new DataSourceNotFoundException("Unable to find data source for given name: " + dataSourceName);
      }
   }

}
