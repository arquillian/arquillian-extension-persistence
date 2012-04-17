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

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.core.configuration.TestConfigurationLoader;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionFeatureResolver;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class PersistenceExtensionFeatureResolverTransactionalTest
{

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void should_have_transactional_support_enabled_by_default() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalSupportEnabledByDefault(),
            TransactionalSupportEnabledByDefault.class.getMethod("shouldPassWithTransactionalSupportEnabledByDefault"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean transactional = persistenceExtensionFeatureResolver.shouldEnableTransaction();

      // then
      assertThat(transactional).isTrue();
   }

   @Test
   public void should_have_transactional_support_disabled_on_method_level() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalSupportEnabledByDefault(),
            TransactionalSupportEnabledByDefault.class.getMethod("shouldPassWithDisabledTransaction"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean transactional = persistenceExtensionFeatureResolver.shouldEnableTransaction();

      // then
      assertThat(transactional).isFalse();
   }

   @Test
   public void should_have_transactional_support_disabled_on_class_level() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalSupportDisabledOnClassLevel(),
            TransactionalSupportDisabledOnClassLevel.class.getMethod("shouldPassWithDisabledTransaction"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean transactional = persistenceExtensionFeatureResolver.shouldEnableTransaction();

      // then
      assertThat(transactional).isFalse();
   }

   @Test
   public void should_obtain_transactional_from_test_method() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClass(),
            TransactionalAnnotatedClass.class.getMethod("shouldPassWithTransactionalRollbackDefinedOnMethodLevel"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean transactional = persistenceExtensionFeatureResolver.shouldEnableTransaction();

      // then
      assertThat(transactional).isTrue();
   }

   @Test
   public void should_obtain_transactional_mode_from_test_method() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClass(),
            TransactionalAnnotatedClass.class.getMethod("shouldPassWithTransactionalRollbackDefinedOnMethodLevel"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TransactionMode transactionalMode = persistenceExtensionFeatureResolver.getTransactionalMode();

      // then
      assertThat(transactionalMode).isEqualTo(TransactionMode.ROLLBACK);
   }

   @Test
   public void should_obtain_transactional_from_class_when_not_defined_for_test() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClass(),
            TransactionalAnnotatedClass.class.getMethod("shouldPassWithoutTransactionalDefinedOnMethodLevel"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      boolean transactional = persistenceExtensionFeatureResolver.shouldEnableTransaction();

      // then
      assertThat(transactional).isTrue();
   }

   @Test
   public void should_obtain_transactional_mode_from_class_when_not_defined_for_test() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClass(),
            TransactionalAnnotatedClass.class.getMethod("shouldPassWithoutTransactionalDefinedOnMethodLevel"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TransactionMode transactionalMode = persistenceExtensionFeatureResolver.getTransactionalMode();

      // then
      assertThat(transactionalMode).isEqualTo(TransactionMode.ROLLBACK);
   }

   @Test
   public void should_have_commit_as_default_transactional_mode_when_not_specified_on_any_level() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClassWithDefaultOnMethodLevel(),
            TransactionalAnnotatedClassWithDefaultOnMethodLevel.class.getMethod("shouldPassWithTransactionalDefaultModeDefinedOnMethodLevel"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      TransactionMode transactionalMode = persistenceExtensionFeatureResolver.getTransactionalMode();

      // then
      assertThat(transactionalMode).isEqualTo(TransactionMode.COMMIT);
   }

   // ----------------------------------------------------------------------------------------
   // Classes used for tests

   @Transactional(TransactionMode.ROLLBACK)
   private static class TransactionalAnnotatedClass
   {
      public void shouldPassWithoutTransactionalDefinedOnMethodLevel()
      {}

      @Transactional(TransactionMode.ROLLBACK)
      public void shouldPassWithTransactionalRollbackDefinedOnMethodLevel()
      {}

   }

   private static class TransactionalAnnotatedClassWithDefaultOnMethodLevel
   {
      @Transactional
      public void shouldPassWithTransactionalDefaultModeDefinedOnMethodLevel()
      {}
   }

   private static class TransactionalSupportEnabledByDefault
   {
      @Transactional(TransactionMode.DISABLED)
      public void shouldPassWithDisabledTransaction()
      {}

      public void shouldPassWithTransactionalSupportEnabledByDefault()
      {}
   }

   @Transactional(TransactionMode.DISABLED)
   private static class TransactionalSupportDisabledOnClassLevel
   {
      public void shouldPassWithDisabledTransaction()
      {}

   }

}
