package org.jboss.arquillian.persistence.client;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.persistence.container.RemotePersistenceExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class PersistenceArchiveAppender implements AuxiliaryArchiveAppender
{

   @Override
   public Archive<?> createAuxiliaryArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "arquillian-persistence.jar")
            // TODO Maven resolver
            .addPackages(
                  true,
                  Filters.exclude(".*/package-info.*"),
                  "org.dbunit")
            .addAsServiceProvider(RemoteLoadableExtension.class, RemotePersistenceExtension.class)
            .addAsManifestResource("datasets/single-user.xml");
   }
}
