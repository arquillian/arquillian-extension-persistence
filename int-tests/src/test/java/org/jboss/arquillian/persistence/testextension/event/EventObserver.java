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
package org.jboss.arquillian.persistence.testextension.event;

import static org.jboss.arquillian.persistence.testextension.event.EventHandlingVerifier.Builder.eventVerifier;

import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.event.CleanupData;
import org.jboss.arquillian.persistence.event.ExecuteScripts;
import org.jboss.arquillian.persistence.event.PersistenceEvent;
import org.jboss.arquillian.persistence.testextension.event.annotation.CleanupShouldBeTriggered;
import org.jboss.arquillian.persistence.testextension.event.annotation.CleanupShouldNotBeTriggered;
import org.jboss.arquillian.persistence.testextension.event.annotation.ExecuteScriptsShouldBeTriggered;
import org.jboss.arquillian.persistence.testextension.event.annotation.ExecuteScriptsShouldNotBeTriggered;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;

public class EventObserver
{
   @Inject @TestScoped
   InstanceProducer<Map<Class<? extends PersistenceEvent>, EventHandlingVerifier>> eventTriggersInstance;

   public void create(@Observes(precedence = 1000000) Before before)
   {
      eventTriggersInstance.set(new HashMap<Class<? extends PersistenceEvent>, EventHandlingVerifier>());

      eventVerifier()
         .definedFor(CleanupData.class)
         .expectedWhen(CleanupShouldBeTriggered.class)
         .notExpectedWhen(CleanupShouldNotBeTriggered.class)
         .registerIfPresent(eventTriggersInstance.get(), before.getTestMethod());

      eventVerifier()
         .definedFor(ExecuteScripts.class)
         .expectedWhen(ExecuteScriptsShouldBeTriggered.class)
         .notExpectedWhen(ExecuteScriptsShouldNotBeTriggered.class)
         .registerIfPresent(eventTriggersInstance.get(), before.getTestMethod());
   }

   public void cleanup(@Observes PersistenceEvent persistenceEvent)
   {
      EventHandlingVerifier verifier = eventTriggersInstance.get().get(persistenceEvent.getClass());
      if (verifier != null)
      {
         verifier.called();
      }
   }

   public void verify(@Observes(precedence = -1000000) After after)
   {
      for (EventHandlingVerifier eventVerifier : eventTriggersInstance.get().values())
      {
         eventVerifier.verifyPhaseWhenEventWasTriggered();
      }
   }


   public void beforeTest(@Observes(precedence = -100000) BeforePersistenceTest beforePersistenceTest)
   {
      for (EventHandlingVerifier eventVerifier : eventTriggersInstance.get().values())
      {
         if (eventVerifier.alreadyTriggered())
         {
            eventVerifier.triggeredBefore();
         }
      }
   }

   public void afterTest(@Observes(precedence = -100000) AfterPersistenceTest afterPersistenceTest)
   {
      for (EventHandlingVerifier eventVerifier : eventTriggersInstance.get().values())
      {
         if (eventVerifier.alreadyTriggered() && !eventVerifier.wasTriggeredBefore())
         {
            eventVerifier.triggeredAfter();
         }
      }
   }

}
