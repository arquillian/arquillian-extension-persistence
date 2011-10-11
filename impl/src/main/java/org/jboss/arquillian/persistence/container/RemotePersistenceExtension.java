package org.jboss.arquillian.persistence.container;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.persistence.PersistenceTestHandler;
import org.jboss.arquillian.persistence.TransactionalWrapper;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitDatasetHandler;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitInitializer;

public class RemotePersistenceExtension implements RemoteLoadableExtension
{

   @Override
   public void register(ExtensionBuilder builder)
   {
      builder.observer(ConfigurationLoader.class)
             .observer(DBUnitDatasetHandler.class) // TODO dispatch automatically ?
             .observer(DBUnitInitializer.class)
             .observer(TransactionalWrapper.class)
             .observer(PersistenceTestHandler.class);
   }

}
