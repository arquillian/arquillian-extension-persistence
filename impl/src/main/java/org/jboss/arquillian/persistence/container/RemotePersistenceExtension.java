package org.jboss.arquillian.persistence.container;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.persistence.PersistenceTestHandler;
import org.jboss.arquillian.persistence.TransactionalWrapper;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitDataStateLogger;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitDatasetHandler;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitPersistenceTestLifecycleHandler;

public class RemotePersistenceExtension implements RemoteLoadableExtension
{

   @Override
   public void register(ExtensionBuilder builder)
   {
      builder.observer(ConfigurationLoader.class)
             .observer(PersistenceTestHandler.class)
             .observer(TransactionalWrapper.class);
      
      builder.observer(DBUnitDatasetHandler.class)
             .observer(DBUnitPersistenceTestLifecycleHandler.class)
             .observer(DBUnitDataStateLogger.class);
   }

}
