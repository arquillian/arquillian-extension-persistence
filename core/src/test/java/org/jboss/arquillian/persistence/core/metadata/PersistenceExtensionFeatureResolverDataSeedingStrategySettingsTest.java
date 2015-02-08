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
package org.jboss.arquillian.persistence.core.metadata;

import org.jboss.arquillian.persistence.DataSeedStrategy;
import org.jboss.arquillian.persistence.SeedDataUsing;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.testutils.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceExtensionFeatureResolverDataSeedingStrategySettingsTest
{

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void should_have_default_data_seeding_strategy() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DefaultDataSeedSettings(),
            DefaultDataSeedSettings.class.getMethod("shouldPass"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      DataSeedStrategy dataSeedStrategy = persistenceExtensionFeatureResolver.getDataSeedStrategy();

      // then
      assertThat(dataSeedStrategy).isEqualTo(DataSeedStrategy.INSERT);
   }

   @Test
   public void should_obtain_data_seeding_strategy_from_class_level_annotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new ClassLevelDataSeedCleanInsertSettings(),
            ClassLevelDataSeedCleanInsertSettings.class.getMethod("shouldPass"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      DataSeedStrategy dataSeedStrategy = persistenceExtensionFeatureResolver.getDataSeedStrategy();

      // then
      assertThat(dataSeedStrategy).isEqualTo(DataSeedStrategy.REFRESH);
   }

   @Test
   public void should_obtain_test_phase_from_method_level() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelDataSeedSettings(),
            MethodLevelDataSeedSettings.class.getMethod("shouldPassUpdateStrategy"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      DataSeedStrategy dataSeedStrategy = persistenceExtensionFeatureResolver.getDataSeedStrategy();

      // then
      assertThat(dataSeedStrategy).isEqualTo(DataSeedStrategy.UPDATE);
   }

   public void should_use_class_level_defined_strategy_when_not_defined_for_given_method() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new MethodLevelDataSeedSettings(),
            MethodLevelDataSeedSettings.class.getMethod("shouldPassUsingClassLevelDefinition"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      DataSeedStrategy dataSeedStrategy = persistenceExtensionFeatureResolver.getDataSeedStrategy();

      // then
      assertThat(dataSeedStrategy).isEqualTo(DataSeedStrategy.INSERT);
   }

   // ----------------------------------------------------------------------------------------
   // Classes used for tests

   @SeedDataUsing(DataSeedStrategy.REFRESH)
   private static class ClassLevelDataSeedCleanInsertSettings
   {
      public void shouldPass()
      {}
   }

   private static class DefaultDataSeedSettings
   {
      public void shouldPass()
      {}
   }

   @SeedDataUsing(DataSeedStrategy.INSERT)
   private static class MethodLevelDataSeedSettings
   {
      @SeedDataUsing(DataSeedStrategy.UPDATE)
      public void shouldPassUpdateStrategy()
      {}

      public void shouldPassUsingClassLevelDefinition()
      {}

   }

}
