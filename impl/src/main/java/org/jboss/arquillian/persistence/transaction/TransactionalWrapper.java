/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.persistence.transaction;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.event.EndTransaction;
import org.jboss.arquillian.persistence.event.StartTransaction;
import org.jboss.arquillian.persistence.exception.ContextNotAvailableException;
import org.jboss.arquillian.persistence.metadata.MetadataProvider;

public class TransactionalWrapper
{

   @Inject
   private Instance<PersistenceConfiguration> configuration;

   @Inject
   private Instance<MetadataProvider> metadataProvider;

   @Inject
   private Instance<Context> contextInstance;

   public void beforeTest(@Observes StartTransaction startTransaction) throws Exception
   {
      obtainTransaction().begin();
   }

   public void afterTest(@Observes EndTransaction endTransaction) throws Exception
   {
      final TransactionMode mode = metadataProvider.get().getTransactionalMode();

      if (TransactionMode.COMMIT.equals(mode))
      {
         obtainTransaction().commit();
      }
      else
      {
         obtainTransaction().rollback();
      }
   }

   private UserTransaction obtainTransaction()
   {
      try
      {
         final Context context = contextInstance.get();
         if (context == null)
         {
            throw new ContextNotAvailableException("No Naming Context available");
         }
         return (UserTransaction) context.lookup(configuration.get().getUserTransactionJndi());
      }
      catch (NamingException e)
      {
         throw new TransactionNotAvailableException("Failed obtaining transaction.", e);
      }
   }


}
