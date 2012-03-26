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
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.naming.PrefixedScriptFileNamingStrategy;
import org.jboss.arquillian.persistence.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.event.CleanupData;
import org.jboss.arquillian.persistence.event.CleanupDataUsingScript;
import org.jboss.arquillian.persistence.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.metadata.PersistenceExtensionFeatureResolver;
import org.jboss.arquillian.persistence.metadata.ValueExtractor;
import org.jboss.arquillian.persistence.metadata.provider.SqlScriptProvider;

public class DataCleanupHandler
{

   @Inject
   private Instance<PersistenceConfiguration> configuration;

   @Inject
   private Instance<MetadataExtractor> metadataExtractor;

   @Inject
   private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolverInstance;

   @Inject
   private Event<CleanupData> cleanUpDataEvent;

   @Inject
   private Event<CleanupDataUsingScript> cleanUpDataUsingScriptEvent;

   public void prepareDatabase(@Observes(precedence = 40) BeforePersistenceTest beforePersistenceTest)
   {
      final PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = persistenceExtensionFeatureResolverInstance.get();

      if (persistenceExtensionFeatureResolver.shouldCleanupBefore())
      {
         cleanUpDataEvent.fire(new CleanupData(beforePersistenceTest, persistenceExtensionFeatureResolver.getCleanupStragety()));
      }

      if (persistenceExtensionFeatureResolver.shouldCleanupUsingScriptBefore())
      {
         final SqlScriptProvider<CleanupUsingScript> scriptsProvider = createScriptProvider();
         cleanUpDataUsingScriptEvent.fire(new CleanupDataUsingScript(beforePersistenceTest, scriptsProvider.getDescriptors(beforePersistenceTest.getTestMethod())));
      }
   }

   public void verifyDatabase(@Observes(precedence = 20) AfterPersistenceTest afterPersistenceTest)
   {
      final PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = persistenceExtensionFeatureResolverInstance.get();

      if (persistenceExtensionFeatureResolver.shouldCleanupAfter())
      {
         cleanUpDataEvent.fire(new CleanupData(afterPersistenceTest, persistenceExtensionFeatureResolver.getCleanupStragety()));
      }

      if (persistenceExtensionFeatureResolver.shouldCleanupUsingScriptAfter())
      {
         final SqlScriptProvider<CleanupUsingScript> scriptsProvider = createScriptProvider();
         cleanUpDataUsingScriptEvent.fire(new CleanupDataUsingScript(afterPersistenceTest, scriptsProvider.getDescriptors(afterPersistenceTest.getTestMethod())));
      }
   }

   // Private methods

   private SqlScriptProvider<CleanupUsingScript> createScriptProvider()
   {
      return SqlScriptProvider
            .forAnnotation(CleanupUsingScript.class)
            .usingConfiguration(configuration.get())
            .extractingMetadataUsing(metadataExtractor.get())
            .namingFollows(new PrefixedScriptFileNamingStrategy("cleanup-", "sql"))
            .build(new ValueExtractor<CleanupUsingScript>()
            {
               @Override
               public String[] extract(CleanupUsingScript toExtract)
               {
                  if (toExtract == null)
                  {
                     return new String[0];
                  }
                  return toExtract.value();
               }
            });
   }

}
