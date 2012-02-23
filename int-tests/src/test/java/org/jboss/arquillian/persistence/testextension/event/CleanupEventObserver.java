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

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.event.CleanupData;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;

public class CleanupEventObserver
{
   @Inject @TestScoped
   InstanceProducer<CleanupEventVerifier> verifierProducer;

   public void create(@Observes(precedence = 1000000) Before before)
   {
      verifierProducer.set(new CleanupEventVerifier());
   }

   public void verify(@Observes(precedence = -1000000) After after)
   {
      verifierProducer.get().verifyPhaseWhenCleanupEventWasCalled();
   }

   public void cleanup(@Observes CleanupData cleanupData)
   {
      verifierProducer.get().called();
   }

   public void beforeTest(@Observes(precedence = -100000) BeforePersistenceTest beforePersistenceTest)
   {
      final CleanupEventVerifier cleanupTestEventVerifier = verifierProducer.get();
      if (cleanupTestEventVerifier.wasCalled())
      {
         cleanupTestEventVerifier.calledBefore();
      }
   }

   public void afterTest(@Observes(precedence = -100000) AfterPersistenceTest afterPersistenceTest)
   {
      final CleanupEventVerifier cleanupTestEventVerifier = verifierProducer.get();
      if (cleanupTestEventVerifier.wasCalled() && !cleanupTestEventVerifier.wasCalledBefore())
      {
         cleanupTestEventVerifier.calledAfter();
      }
   }

}
