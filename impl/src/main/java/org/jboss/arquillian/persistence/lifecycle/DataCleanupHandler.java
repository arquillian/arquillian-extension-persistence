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
import org.jboss.arquillian.persistence.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.event.CleanupData;
import org.jboss.arquillian.persistence.metadata.MetadataProvider;

public class DataCleanupHandler
{

   @Inject
   private Instance<MetadataProvider> metadataProviderInstance;

   @Inject
   private Event<CleanupData> cleanUpDataEvent;

   public void prepareDatabase(@Observes(precedence = 40) BeforePersistenceTest beforePersistenceTest)
   {
      final MetadataProvider metadataProvider = metadataProviderInstance.get();

      if (metadataProvider.shouldCleanupBefore())
      {
         cleanUpDataEvent.fire(new CleanupData(beforePersistenceTest));
      }
   }

   public void verifyDatabase(@Observes(precedence = 20) AfterPersistenceTest afterPersistenceTest)
   {
      final MetadataProvider metadataProvider = metadataProviderInstance.get();

      if (metadataProvider.shouldCleanupAfter())
      {
         cleanUpDataEvent.fire(new CleanupData(afterPersistenceTest));
      }

   }

}
