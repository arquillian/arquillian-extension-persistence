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
package org.jboss.arquillian.persistence.core.lifecycle;

import java.util.Collection;

import org.jboss.arquillian.container.test.spi.command.CommandService;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.persistence.core.command.SchemaCreationControlCommand;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.core.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.core.data.provider.SqlScriptProvider;
import org.jboss.arquillian.persistence.core.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.core.event.ExecuteScripts;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionFeatureResolver;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class SchemaCreationScriptsExecutor
{
   @Inject
   private Instance<PersistenceConfiguration> configuration;

   @Inject
   private Event<ExecuteScripts> executeScriptsEvent;

   @Inject
   private Instance<CommandService> commandService;

   @Inject
   private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolver;

   public void createSchema(@Observes(precedence = 10) EventContext<BeforePersistenceTest> context)
   {
      final BeforePersistenceTest beforePersistenceTest = context.getEvent();
      if (persistenceExtensionFeatureResolver.get().shouldCreateSchema() && !schemaCreated(beforePersistenceTest))
      {
         final Collection<SqlScriptResourceDescriptor> schemaDescriptors = SqlScriptProvider.createProviderForCreateSchemaScripts(beforePersistenceTest.getTestClass(), configuration.get()).getDescriptors(beforePersistenceTest.getTestClass());
         if (!schemaDescriptors.isEmpty())
         {
            executeScriptsEvent.fire(new ExecuteScripts(beforePersistenceTest, schemaDescriptors));
         }
      }
      context.proceed();
   }

   // Private methods

   private boolean schemaCreated(final TestEvent beforePersistenceTest)
   {
      return commandService.get().execute(new SchemaCreationControlCommand(beforePersistenceTest.getTestInstance().getClass().getSimpleName()));
   }
}
