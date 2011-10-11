package org.jboss.arquillian.persistence.container;

import org.jboss.arquillian.container.test.spi.command.CommandService;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.persistence.command.ConfigurationCommand;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

/**
 * Loads configuration from the client
 * @author bmajsak
 *
 */
public class ConfigurationLoader
{

   @Inject
   private Instance<ServiceLoader> serviceLoader;

   @Inject @SuiteScoped
   private InstanceProducer<PersistenceConfiguration> configuration;

   public void fetchConfiguration(@Observes BeforeSuite beforeSuite)
   {
      ConfigurationCommand command = new ConfigurationCommand();
      configuration.set(getCommandService().execute(command));
   }

   private CommandService getCommandService()
   {
      ServiceLoader loader = serviceLoader.get();
      if(loader == null)
      {
         throw new IllegalStateException("No " + ServiceLoader.class.getName() + " found in context");
      }
      
      CommandService service = loader.onlyOne(CommandService.class);
      if(service == null)
      {
         throw new IllegalStateException("No " + CommandService.class.getName() + " found in context");
      }
      
      return service;
   }
   
}
