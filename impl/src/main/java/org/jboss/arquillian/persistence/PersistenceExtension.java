package org.jboss.arquillian.persistence;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.persistence.client.PersistenceArchiveAppender;
import org.jboss.arquillian.persistence.configuration.PersistenceConfigurationProducer;

/**
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a> 
 *
 */
public class PersistenceExtension implements LoadableExtension {

   @Override
   public void register(ExtensionBuilder builder) 
   {
      builder.service(AuxiliaryArchiveAppender.class, PersistenceArchiveAppender.class)
             .observer(PersistenceConfigurationProducer.class);
   }

}
