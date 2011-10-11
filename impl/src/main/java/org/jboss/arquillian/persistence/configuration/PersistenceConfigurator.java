package org.jboss.arquillian.persistence.configuration;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.command.ConfigurationCommand;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

/**
 * 
 * TODO extend javadoc
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceConfigurator
{
   @Inject
   private Instance<ArquillianDescriptor> descriptor;
   
   @Inject @SuiteScoped
   InstanceProducer<PersistenceConfiguration> configurationProducer;
   
   @Inject @SuiteScoped
   Instance<PersistenceConfiguration> configuration;

   public void configure(@Observes BeforeClass beforeClassEvent)
   {
      ConfigurationExtractor extractor = new ConfigurationExtractor(descriptor.get());
      PersistenceConfiguration configuration = extractor.extract();
      configurationProducer.set(configuration);
      System.out.println("--> Configuration loaded");
   }
   
   public void listen(@Observes ConfigurationCommand configurationCommand)
   {
      configurationCommand.setResult(configuration.get());
   }

}
