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
import org.jboss.arquillian.persistence.event.CleanUpDataEvent;
import org.jboss.arquillian.persistence.event.PrepareDataEvent;
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
   
   @Inject
   private Event<PrepareDataEvent> prepareDataEvent;
   
   @Inject
   private Event<CleanUpDataEvent> cleanUpDataEvent;
   
   private MetadataProvider metadataProvider;
   
   public void beforeTest(@Observes Before beforeTestEvent)
   {
      metadataProvider = new MetadataProvider(beforeTestEvent, configuration.get());
      if (!metadataProvider.isPersistenceInteractionApplicable())
      {
         return;
      }

      propagateDataSource();
      
      prepareDataEvent.fire(new PrepareDataEvent(metadataProvider.dataSetFile(), metadataProvider.dataSetType()));
   }

   public void afterTest(@Observes After afterTestEvent)
   {
      metadataProvider = new MetadataProvider(afterTestEvent, configuration.get());
      if (!metadataProvider.isPersistenceInteractionApplicable())
      {
         return;
      }
      
      if (!metadataProvider.isTransactional() || TransactionMode.ROLLBACK.equals(metadataProvider.getTransactionalMode()))
      {
         cleanUpDataEvent.fire(new CleanUpDataEvent());
      }
   }

   // Private methods
   
   private void propagateDataSource()
   {
      DataSource dataSource = loadDataSource(metadataProvider.getDataSourceName());
      dataSourceProducer.set(dataSource);
   }

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
