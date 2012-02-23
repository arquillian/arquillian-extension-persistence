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

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.TestExecutionPhase;

public class CleanupEventVerifier
{

   private boolean calledBefore;

   private boolean calledAfter;

   private boolean called;

   private TestExecutionPhase phase = TestExecutionPhase.NONE;

   private TestExecutionPhase expectedPhase;

   private boolean verificationRequested;

   public boolean wasCalledAfter()
   {
      return calledAfter;
   }

   public void calledAfter()
   {
      phase = TestExecutionPhase.AFTER;
      this.calledAfter = true;
   }

   public void dataCleanupShouldBePerformedAfterTest()
   {
      verificationRequested = true;
      expectedPhase = TestExecutionPhase.AFTER;
   }

   public boolean wasCalled()
   {
      return called;
   }

   public void called()
   {
      this.called = true;
   }

   public boolean wasCalledBefore()
   {
      return calledBefore;
   }

   public void calledBefore()
   {
      phase = TestExecutionPhase.BEFORE;
      this.calledBefore = true;
   }

   public void dataCleanupShouldBePerformedBeforeTest()
   {
      verificationRequested = true;
      expectedPhase = TestExecutionPhase.BEFORE;
   }

   public void dataCleanupShouldNotBePerformed()
   {
      verificationRequested = true;
      expectedPhase = TestExecutionPhase.NONE;
   }

   public void verifyPhaseWhenCleanupEventWasCalled()
   {
      if (verificationRequested)
      {
         assertThat(phase).describedAs("Verifying database cleanup event test phase")
                          .isEqualTo(expectedPhase);
      }
   }

}
