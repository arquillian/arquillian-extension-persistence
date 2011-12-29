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
package org.jboss.arquillian.persistence.container;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.persistence.configuration.RemotePersistenceConfigurationProducer;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitDataStateLogger;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitDatasetHandler;
import org.jboss.arquillian.persistence.data.dbunit.DBUnitPersistenceTestLifecycleHandler;
import org.jboss.arquillian.persistence.lifecycle.CustomScriptsAroundTestExecutor;
import org.jboss.arquillian.persistence.lifecycle.DataSourceProducer;
import org.jboss.arquillian.persistence.lifecycle.DatasetHandler;
import org.jboss.arquillian.persistence.lifecycle.ErrorCollectorHandler;
import org.jboss.arquillian.persistence.lifecycle.PersistenceTestHandler;
import org.jboss.arquillian.persistence.lifecycle.TransactionHandler;
import org.jboss.arquillian.persistence.transaction.TransactionalWrapper;

/**
 * Defines all the bindings for Arquillian extension run in the
 * container.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class RemotePersistenceExtension implements RemoteLoadableExtension
{

   @Override
   public void register(ExtensionBuilder builder)
   {
      registerTestLifecycleHandlers(builder);
      registerDBUnitHandlers(builder);

      builder.observer(RemotePersistenceConfigurationProducer.class)
             .observer(CommandServiceProducer.class)
             .observer(TransactionalWrapper.class);


   }

   private void registerDBUnitHandlers(ExtensionBuilder builder)
   {
      builder.observer(DBUnitDatasetHandler.class)
             .observer(DBUnitPersistenceTestLifecycleHandler.class)
             .observer(DBUnitDataStateLogger.class);
   }

   private void registerTestLifecycleHandlers(ExtensionBuilder builder)
   {
      builder.observer(PersistenceTestHandler.class)
             .observer(CustomScriptsAroundTestExecutor.class)
             .observer(DatasetHandler.class)
             .observer(DataSourceProducer.class)
             .observer(ErrorCollectorHandler.class)
             .observer(TransactionHandler.class);
   }

}
