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
package org.jboss.arquillian.persistence.metadata;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class MetadataProviderCleanupSettingsTest
{

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();


   @Test
   public void shouldHaveDefaultCleanupTestPhaseSetToBefore() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DefaultCleanupSettings(),
            DefaultCleanupSettings.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = metadataProvider.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.BEFORE);
   }

   @Test
   public void shouldObtainCleanupTestPhaseFromClassLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new ClassLevelCleanupAfterSettings(),
            ClassLevelCleanupAfterSettings.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = metadataProvider.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.AFTER);
   }

   @Test
   public void shouldObtainTestPhaseFromMethodLevelAnnotationContainingBothPhaseAndMode() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassCleanupAndAfterPhaseDefined"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = metadataProvider.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.AFTER);
   }

   @Test
   public void shouldUseDefaultTestPhaseFromMethodLevelAnnotationContainingMode() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassModeOnly"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = metadataProvider.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.getDefault());
   }

   @Test
   public void shouldObtainTestPhaseFromMethodLevelAnnotationContainingPhase() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassPhaseOnly"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = metadataProvider.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.NONE);
   }

   @Test
   public void shouldCleanupBeforeIfNoAnnotationDefined() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DefaultCleanupSettings(),
            DefaultCleanupSettings.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean shouldCleanupBefore = metadataProvider.shouldCleanupBefore();

      // then
      assertThat(shouldCleanupBefore).isTrue();
   }

   @Test
   public void shouldCleanupAfterIfPhaseIsDefined() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassCleanupAndAfterPhaseDefined"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean shouldCleanupAfter = metadataProvider.shouldCleanupAfter();

      // then
      assertThat(shouldCleanupAfter).isTrue();
   }

   @Cleanup(phase = TestExecutionPhase.AFTER)
   private static class ClassLevelCleanupAfterSettings
   {
      public void shouldPass()
      {}
   }

   private static class DefaultCleanupSettings
   {
      public void shouldPass()
      {}
   }

   private static class MethodLevelCleanupSettings
   {
      @Cleanup(phase = TestExecutionPhase.AFTER)
      public void shouldPassCleanupAndAfterPhaseDefined()
      {}

      @Cleanup
      public void shouldPassModeOnly()
      {}

      @Cleanup(phase = TestExecutionPhase.NONE)
      public void shouldPassPhaseOnly()
      {}

   }

}
