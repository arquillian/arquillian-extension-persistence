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
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class PersistenceExtensionFeatureResolverCleanupSettingsTest
{

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void should_have_default_cleanup_test_phase_set_to_before() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DefaultCleanupSettings(),
            DefaultCleanupSettings.class.getMethod("shouldPass"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = persistenceExtensionFeatureResolver.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.BEFORE);
   }

   @Test
   public void should_obtain_cleanup_test_phase_from_class_level_annotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new ClassLevelCleanupAfterSettings(),
            ClassLevelCleanupAfterSettings.class.getMethod("shouldPass"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = persistenceExtensionFeatureResolver.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.AFTER);
   }

   @Test
   public void should_obtain_test_phase_from_method_level_annotation_containing_both_phase_and_mode() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassCleanupAndAfterPhaseDefined"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = persistenceExtensionFeatureResolver.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.AFTER);
   }

   @Test
   public void should_use_default_test_phase_from_method_level_annotation_containing_mode() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassStrategyOnly"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = persistenceExtensionFeatureResolver.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.getDefault());
   }

   @Test
   public void should_obtain_test_phase_from_method_level_annotation_containing_phase() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassPhaseOnly"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TestExecutionPhase phase = persistenceExtensionFeatureResolver.getCleanupTestPhase();

      // then
      assertThat(phase).isEqualTo(TestExecutionPhase.NONE);
   }

   @Test
   public void should_cleanup_before_when_no_annotation_defined() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DefaultCleanupSettings(),
            DefaultCleanupSettings.class.getMethod("shouldPass"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean shouldCleanupBefore = persistenceExtensionFeatureResolver.shouldCleanupBefore();

      // then
      assertThat(shouldCleanupBefore).isTrue();
   }

   @Test
   public void should_cleanup_after_when_phase_is_defined() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassCleanupAndAfterPhaseDefined"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean shouldCleanupAfter = persistenceExtensionFeatureResolver.shouldCleanupAfter();

      // then
      assertThat(shouldCleanupAfter).isTrue();
   }

   @Test
   public void should_cleanup_using_script_when_defined_on_method_level() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new CleanupUsingScriptOnMethodLevelSettings(),
            CleanupUsingScriptOnMethodLevelSettings.class.getMethod("shouldPassWhenCleanupUsingScriptDefined"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean shouldCleanupUsingScriptAfter = persistenceExtensionFeatureResolver.shouldCleanupUsingScriptAfter();

      // then
      assertThat(shouldCleanupUsingScriptAfter).isTrue();
   }

   @Test
   public void should_have_strict_cleanup_strategy_as_default() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassUsingDefaults"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      CleanupStrategy cleanupStragety = persistenceExtensionFeatureResolver.getCleanupStragety();

      // then
      assertThat(cleanupStragety).isEqualTo(CleanupStrategy.STRICT);
   }

   @Test
   public void should_fetch_cleanup_strategy_from_test() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelCleanupSettings(),
            MethodLevelCleanupSettings.class.getMethod("shouldPassStrategyOnly"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      CleanupStrategy cleanupStragety = persistenceExtensionFeatureResolver.getCleanupStragety();

      // then
      assertThat(cleanupStragety).isEqualTo(CleanupStrategy.USED_ROWS_ONLY);
   }

   // ----------------------------------------------------------------------------------------
   // Classes used for tests

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

   @Cleanup
   private static class CleanupUsingScriptOnMethodLevelSettings
   {
      @CleanupUsingScript(value = "clean.sql", phase = TestExecutionPhase.AFTER)
      public void shouldPassWhenCleanupUsingScriptDefined()
      {}
   }

   private static class MethodLevelCleanupSettings
   {
      @Cleanup(phase = TestExecutionPhase.AFTER)
      public void shouldPassCleanupAndAfterPhaseDefined()
      {}

      @Cleanup(strategy = CleanupStrategy.USED_ROWS_ONLY)
      public void shouldPassStrategyOnly()
      {}

      @Cleanup(phase = TestExecutionPhase.NONE)
      public void shouldPassPhaseOnly()
      {}

      public void shouldPassUsingDefaults()
      {}

   }

}
