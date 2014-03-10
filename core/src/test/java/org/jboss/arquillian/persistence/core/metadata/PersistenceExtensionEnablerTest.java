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

import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionEnabler;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class PersistenceExtensionEnablerTest
{

   @Test
   public void should_not_accept_class_without_data_annotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new NonPersistenceTest(),
            NonPersistenceTest.class.getMethod("shouldPass"));
      PersistenceExtensionEnabler metadataProvider = new PersistenceExtensionEnabler(testEvent.getTestClass());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.shouldPersistenceExtensionBeActivated();

      // then
      assertThat(persistenceFeatureEnabled).isFalse();
   }

   @Test
   public void should_accept_class_with_persistence_test_annotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new PersistenceTestClass(),
            PersistenceTestClass.class.getMethod("shouldPass"));
      PersistenceExtensionEnabler metadataProvider = new PersistenceExtensionEnabler(testEvent.getTestClass());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.shouldPersistenceExtensionBeActivated();

      // then
      assertThat(persistenceFeatureEnabled).isTrue();
   }

   @Test
   public void should_accept_class_with_script_annotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new PersistenceTestWithScriptAnnotation(),
            PersistenceTestWithScriptAnnotation.class.getMethod("shouldPass"));
      PersistenceExtensionEnabler metadataProvider = new PersistenceExtensionEnabler(testEvent.getTestClass());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.shouldPersistenceExtensionBeActivated();

      // then
      assertThat(persistenceFeatureEnabled).isTrue();
   }

   @Test
   public void should_accept_class_with_expected_annotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new PersistenceTestWithExpectedAnnotation(),
            PersistenceTestWithExpectedAnnotation.class.getMethod("shouldPass"));
      PersistenceExtensionEnabler metadataProvider = new PersistenceExtensionEnabler(testEvent.getTestClass());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.shouldPersistenceExtensionBeActivated();

      // then
      assertThat(persistenceFeatureEnabled).isTrue();
   }

   @Test
   public void shoul_accept_class_without_data_source_annotation_but_defined_in_properties() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      PersistenceExtensionEnabler metadataProvider = new PersistenceExtensionEnabler(testEvent.getTestClass());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.shouldPersistenceExtensionBeActivated();

      // then
      assertThat(persistenceFeatureEnabled).isTrue();
   }

   // ----------------------------------------------------------------------------------------
   // Classes used for tests

   @UsingDataSet
   private static class DataSourceExpectedFromDefaultConfiguration
   {
      public void shouldPass() {}
   }

   @PersistenceTest
   private static class PersistenceTestClass
   {
      public void shouldPass() {}
   }

   private static class PersistenceTestWithExpectedAnnotation
   {
      @ShouldMatchDataSet
      public void shouldPass() {}
   }

   private static class PersistenceTestWithScriptAnnotation
   {
      @ApplyScriptBefore
      public void shouldPass() {}
   }

   private static class NonPersistenceTest
   {
      public void shouldPass() {}
   }
}
