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

import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

@SuppressWarnings("unused")
public class PersistenceExtensionFeatureResolverDataSourceTest
{

   private static final String DATA_SOURCE_ON_CLASS_LEVEL = "dataSourceOnClassLevel";

   private static final String DATA_SOURCE_ON_METHOD_LEVEL = "dataSourceOnMethodLevel";

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test(expected = DataSourceNotDefinedException.class)
   public void should_thrown_exception_when_test_is_expecting_persistence_feature_but_does_not_have_data_source_defined() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), TestConfigurationLoader.createPersistenceConfigurationFrom("arquillian-without-persistence-properties.xml"));

      // when
      String dataSourceName = persistenceExtensionFeatureResolver.getDataSourceName();

      // then
      // exception should be thrown
   }

   @Test
   public void should_fetch_data_source_name_from_test() throws Exception
   {
      // given
      String expectedDataSourceName = DATA_SOURCE_ON_METHOD_LEVEL;
      TestEvent testEvent = new TestEvent(new DataSourceAnnotated(),
            DataSourceAnnotated.class.getMethod("shouldPassWithDataSourceDefinedOnMethodLevel"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      String dataSourceName = persistenceExtensionFeatureResolver.getDataSourceName();

      // then
      assertThat(dataSourceName).isEqualTo(expectedDataSourceName);
   }

   @Test
   public void should_fetch_data_source_name_from_class_level_if_not_defined_for_test() throws Exception
   {
      // given
      String expectedDataSourceName = DATA_SOURCE_ON_CLASS_LEVEL;
      TestEvent testEvent = new TestEvent(new DataSourceAnnotated(),
            DataSourceAnnotated.class.getMethod("shouldPassWithoutDataSourceDefinedOnMethodLevel"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      String dataSourceName = persistenceExtensionFeatureResolver.getDataSourceName();

      // then
      assertThat(dataSourceName).isEqualTo(expectedDataSourceName);
   }

   @Test
   public void should_fetch_data_source_from_properties_when_not_define_on_test_or_class_level() throws Exception
   {
      // given
      String expectedDataSourceName = "Ike";
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      String dataSourceName = persistenceExtensionFeatureResolver.getDataSourceName();

      // then
      assertThat(dataSourceName).isEqualTo(expectedDataSourceName);
   }

   @Test(expected = DataSourceNotDefinedException.class)
   public void should_throw_exception_when_data_source_is_not_defined_in_property_file_and_class_and_method() throws Exception
   {
      // given
      String expectedDataSourceName = "Ike";
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = new PersistenceExtensionFeatureResolver(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), TestConfigurationLoader.createPersistenceConfigurationFrom("arquillian-without-persistence-properties.xml"));

      // when
      String dataSourceName = persistenceExtensionFeatureResolver.getDataSourceName();

      // then
      // exception should be thrown
   }

   // ----------------------------------------------------------------------------------------
   // Classes used for tests

   @DataSource(DATA_SOURCE_ON_CLASS_LEVEL)
   private static class DataSourceAnnotated
   {
      public void shouldPassWithoutDataSourceDefinedOnMethodLevel() {}

      @DataSource(DATA_SOURCE_ON_METHOD_LEVEL)
      public void shouldPassWithDataSourceDefinedOnMethodLevel() {}
   }

   @UsingDataSet
   private static class DataSourceExpectedFromDefaultConfiguration
   {
      public void shouldPass() {}
   }

}
