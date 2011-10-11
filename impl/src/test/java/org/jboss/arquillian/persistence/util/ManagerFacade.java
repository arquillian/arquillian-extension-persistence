package org.jboss.arquillian.persistence.util;

import org.jboss.arquillian.core.impl.ManagerImpl;
import org.jboss.arquillian.core.spi.context.Context;

public class ManagerFacade
{

   private final ManagerImpl manager;

   public ManagerFacade(ManagerImpl managerImpl)
   {
      this.manager = managerImpl;
   }

   public <T> T getInstance(Class<T> cls, Class<? extends Context> context)
   {
      return manager.getContext(context).getObjectStore().get(cls);
   }

}
