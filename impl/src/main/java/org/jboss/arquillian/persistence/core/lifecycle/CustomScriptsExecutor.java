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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.core.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.core.event.ApplyScriptsAfterTest;
import org.jboss.arquillian.persistence.core.event.ApplyScriptsBeforeTest;
import org.jboss.arquillian.persistence.core.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.core.util.Strings;
import org.jboss.arquillian.persistence.script.ScriptLoader;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.persistence.script.data.descriptor.FileSqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.script.data.descriptor.InlineSqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.script.data.descriptor.SqlScriptResourceDescriptor;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class CustomScriptsExecutor
{
   @Inject
   private Instance<ScriptingConfiguration> scriptingConfigurationInstance;

   @Inject
   private Event<ApplyScriptsBeforeTest> applyScriptsBeforeTestEvent;

   @Inject
   private Event<ApplyScriptsAfterTest> applyScriptsAfterTestEvent;

   public void executeBeforeTest(@Observes(precedence = 50) BeforePersistenceTest beforePersistenceTest)
   {
      executeScriptsBeforeTest(beforePersistenceTest);
   }

   public void executeAfterTest(@Observes(precedence = 10) AfterPersistenceTest afterPersistenceTest)
   {
      executeScriptsAfterTest(afterPersistenceTest);
   }

   // Private methods

   private void executeScriptsBeforeTest(BeforePersistenceTest beforePersistenceTest)
   {
      final ScriptingConfiguration configuration = scriptingConfigurationInstance.get();
      String[] scriptsToExecuteBeforeTest = configuration.getScriptsToExecuteBeforeTest();
      final List<SqlScriptResourceDescriptor> scripts = processScripts(scriptsToExecuteBeforeTest);
      applyScriptsBeforeTestEvent.fire(new ApplyScriptsBeforeTest(beforePersistenceTest, scripts));
   }

   private void executeScriptsAfterTest(AfterPersistenceTest afterPersistenceTest)
   {
      final ScriptingConfiguration configuration = scriptingConfigurationInstance.get();
      String[] scriptsToExecuteAfterTest = configuration.getScriptsToExecuteAfterTest();
      final List<SqlScriptResourceDescriptor> scripts = processScripts(scriptsToExecuteAfterTest);
      applyScriptsAfterTestEvent.fire(new ApplyScriptsAfterTest(afterPersistenceTest, scripts));
   }

   private List<SqlScriptResourceDescriptor> processScripts(String[] scripts)
   {
      if (scripts == null)
      {
         return Collections.emptyList();
      }

      final List<SqlScriptResourceDescriptor> processedScripts = new ArrayList<SqlScriptResourceDescriptor>();
      for (String script : scripts)
      {
         if (ScriptLoader.isSqlScriptFile(script))
         {
            processedScripts.add(new FileSqlScriptResourceDescriptor(script));
         }
         else if (!Strings.isEmpty(script))
         {
            processedScripts.add(new InlineSqlScriptResourceDescriptor(script));
         }
      }
      return processedScripts;
   }

}
