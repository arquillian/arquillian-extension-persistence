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

import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.ConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class MetadataProviderTransactionalTest
{

   private PersistenceConfiguration defaultConfiguration = ConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldHaveTransactionalSupportEnabledByDefault() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalSupportEnabledByDefault(),
            TransactionalSupportEnabledByDefault.class.getMethod("shouldPassWithTransactionalSupportEnabledByDefault"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      boolean transactional = metadataProvider.isTransactional();

      // then
      assertThat(transactional).isTrue();
   }
   
   @Test
   public void shouldHaveTransactionalSupportDisabledOnMethodLevel() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalSupportEnabledByDefault(),
            TransactionalSupportEnabledByDefault.class.getMethod("shouldPassWithDisabledTransaction"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      boolean transactional = metadataProvider.isTransactional();

      // then
      assertThat(transactional).isFalse();
   }
   
   @Test
   public void shouldHaveTransactionalSupportDisabledOnClassLevel() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalSupportDisabledOnClassLevel(),
            TransactionalSupportDisabledOnClassLevel.class.getMethod("shouldPassWithDisabledTransaction"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      boolean transactional = metadataProvider.isTransactional();

      // then
      assertThat(transactional).isFalse();
   }
   
   @Test
   public void shouldObtainTransactionalFromTestMethod() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClass(),
            TransactionalAnnotatedClass.class.getMethod("shouldPassWithTransactionalRollbackDefinedOnMethodLevel"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      boolean transactional = metadataProvider.isTransactional();

      // then
      assertThat(transactional).isTrue();
   }

   @Test
   public void shouldObtainTransactionalModeFromTestMethod() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClass(),
            TransactionalAnnotatedClass.class.getMethod("shouldPassWithTransactionalRollbackDefinedOnMethodLevel"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      TransactionMode transactionalMode = metadataProvider.getTransactionalMode();

      // then
      assertThat(transactionalMode).isEqualTo(TransactionMode.ROLLBACK);
   }

   @Test
   public void shouldObtainTransactionalFromClassWhenNotDefinedForTest() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClass(),
            TransactionalAnnotatedClass.class.getMethod("shouldPassWithoutTransactionalDefinedOnMethodLevel"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      boolean transactional = metadataProvider.isTransactional();

      // then
      assertThat(transactional).isTrue();
   }

   @Test
   public void shouldObtainTransactionalModeFromClassWhenNotDefinedForTest() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClass(),
            TransactionalAnnotatedClass.class.getMethod("shouldPassWithoutTransactionalDefinedOnMethodLevel"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      TransactionMode transactionalMode = metadataProvider.getTransactionalMode();

      // then
      assertThat(transactionalMode).isEqualTo(TransactionMode.ROLLBACK);
   }

   @Test
   public void shouldHaveCommitAsDefaultTransactionalModeWhenNotSpecifiedOnAnyLevel() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClassWithDefaultOnMethodLevel(),
            TransactionalAnnotatedClassWithDefaultOnMethodLevel.class.getMethod("shouldPassWithTransactionalDefaultModeDefinedOnMethodLevel"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      TransactionMode transactionalMode = metadataProvider.getTransactionalMode();

      // then
      assertThat(transactionalMode).isEqualTo(TransactionMode.COMMIT);
   }
   
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
