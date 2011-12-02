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

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class MetadataProviderPersistentFeatureTest
{

   @Test
   public void shouldNotAcceptClassWithoutDataAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new NonPersistenceTest(),
            NonPersistenceTest.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), TestConfigurationLoader.createDefaultConfiguration());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.isPersistenceFeatureEnabled();

      // then
      assertThat(persistenceFeatureEnabled).isFalse();
   }

   @Test
   public void shouldAcceptClassWithPersistenceTestAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new PersistenceTestClass(),
            PersistenceTestClass.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), TestConfigurationLoader.createDefaultConfiguration());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.isPersistenceFeatureEnabled();

      // then
      assertThat(persistenceFeatureEnabled).isTrue();
   }

   @Test
   public void shouldAcceptClassWithExpectedAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new PersistenceTestWithExpectedAnnotation(),
            PersistenceTestWithExpectedAnnotation.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), TestConfigurationLoader.createDefaultConfiguration());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.isPersistenceFeatureEnabled();

      // then
      assertThat(persistenceFeatureEnabled).isTrue();
   }

   @Test
   public void shoulAcceptClassWithoutDataSourceAnnotationButDefinedInProperties() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), TestConfigurationLoader.createDefaultConfiguration());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.isPersistenceFeatureEnabled();

      // then
      assertThat(persistenceFeatureEnabled).isTrue();
   }

   @Data
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
      @Expected
      public void shouldPass() {}
   }

   private static class NonPersistenceTest
   {
      public void shouldPass() {}
   }



}
