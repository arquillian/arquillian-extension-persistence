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

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.core.data.script.ScriptHelper;
import org.jboss.arquillian.persistence.core.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.core.event.ApplyCleanupStatement;
import org.jboss.arquillian.persistence.core.event.ApplyInitStatement;
import org.jboss.arquillian.persistence.core.event.BeforePersistenceTest;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class CustomScriptsExecutor
{

   @Inject
   private Instance<PersistenceConfiguration> configuration;

   @Inject
   private Event<ApplyInitStatement> applyInitStatementEvent;

   @Inject
   private Event<ApplyCleanupStatement> applyCleanupStatementEvent;

   public void executeBeforeTest(@Observes(precedence = 50) BeforePersistenceTest beforePersistenceTest)
   {
      executeInitStatement();
   }

   public void executeAfterTest(@Observes(precedence = 10) AfterPersistenceTest afterPersistenceTest)
   {
      executeCleanupStatement();
   }

   // Private methods

   private void executeInitStatement()
   {
      final PersistenceConfiguration persistenceConfiguration = configuration.get();
      String initStatement = persistenceConfiguration.getInitStatement();
      if (ScriptHelper.isSqlScriptFile(initStatement))
      {
         initStatement = ScriptHelper.loadScript(initStatement);
      }
      applyInitStatementEvent.fire(new ApplyInitStatement(initStatement));
   }

   private void executeCleanupStatement()
   {
      final PersistenceConfiguration persistenceConfiguration = configuration.get();
      String cleanupStatement = persistenceConfiguration.getCleanupStatement();
      if (ScriptHelper.isSqlScriptFile(cleanupStatement))
      {
         cleanupStatement = ScriptHelper.loadScript(cleanupStatement);
      }
      applyCleanupStatementEvent.fire(new ApplyCleanupStatement(cleanupStatement));
   }

}
