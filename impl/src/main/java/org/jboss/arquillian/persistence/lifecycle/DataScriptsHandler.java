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
package org.jboss.arquillian.persistence.lifecycle;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.naming.PrefixedScriptFileNamingStrategy;
import org.jboss.arquillian.persistence.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.event.ExecuteScripts;
import org.jboss.arquillian.persistence.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.metadata.MetadataProvider;
import org.jboss.arquillian.persistence.metadata.SqlScriptProvider;
import org.jboss.arquillian.persistence.metadata.ValueExtractor;

public class DataScriptsHandler
{

   @Inject
   private Instance<PersistenceConfiguration> configuration;

   @Inject
   private Instance<MetadataExtractor> metadataExtractor;

   @Inject
   private Instance<MetadataProvider> metadataProvider;

   @Inject
   private Event<ExecuteScripts> executeScriptsEvent;

   public void executeBeforeTest(@Observes(precedence = 30) BeforePersistenceTest beforePersistenceTest)
   {
      executeCustomScriptsBefore(beforePersistenceTest);
   }

   public void executeAfterTest(@Observes(precedence = 40) AfterPersistenceTest afterPersistenceTest)
   {
      executeCustomScriptsAfter(afterPersistenceTest);
   }

   // Private methods

   private void executeCustomScriptsBefore(BeforePersistenceTest beforePersistenceTest)
   {
      if (!metadataProvider.get().isCustomScriptToBeAppliedBeforeTestRequested())
      {
         return;
      }

      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = SqlScriptProvider
            .forAnnotation(ApplyScriptBefore.class)
            .usingConfiguration(configuration.get())
            .extractingMetadataUsing(metadataExtractor.get())
            .namingFollows(new PrefixedScriptFileNamingStrategy("before-", "sql"))
            .build(new ValueExtractor<ApplyScriptBefore>()
            {
               @Override
               public String[] extract(ApplyScriptBefore toExtract)
               {
                  if (toExtract == null)
                  {
                     return new String[0];
                  }
                  return toExtract.value();
               }
            });

      executeScriptsEvent.fire(new ExecuteScripts(beforePersistenceTest, scriptsProvider.getDescriptors(beforePersistenceTest.getTestMethod())));
   }

   private void executeCustomScriptsAfter(AfterPersistenceTest afterPersistenceTest)
   {
      if (!metadataProvider.get().isCustomScriptToBeAppliedAfterTestRequested())
      {
         return;
      }

      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = SqlScriptProvider
            .forAnnotation(ApplyScriptAfter.class)
            .usingConfiguration(configuration.get())
            .extractingMetadataUsing(metadataExtractor.get())
            .namingFollows(new PrefixedScriptFileNamingStrategy("after-", "sql"))
            .build(new ValueExtractor<ApplyScriptAfter>()
            {
               @Override
               public String[] extract(ApplyScriptAfter toExtract)
               {
                  if (toExtract == null)
                  {
                     return new String[0];
                  }
                  return toExtract.value();
               }
            });

      executeScriptsEvent.fire(new ExecuteScripts(afterPersistenceTest, scriptsProvider.getDescriptors(afterPersistenceTest.getTestMethod())));
   }
}
