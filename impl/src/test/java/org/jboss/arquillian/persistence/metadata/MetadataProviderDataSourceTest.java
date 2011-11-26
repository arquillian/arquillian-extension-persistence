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
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.client.ConfigurationLoader;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

@SuppressWarnings("unused")
public class MetadataProviderDataSourceTest
{

   private static final String DATA_SOURCE_ON_CLASS_LEVEL = "dataSourceOnClassLevel";

   private static final String DATA_SOURCE_ON_METHOD_LEVEL = "dataSourceOnMethodLevel";

   private PersistenceConfiguration defaultConfiguration = ConfigurationLoader.createDefaultConfiguration();

   @Test(expected = DataSourceNotDefinedException.class)
   public void shouldThrownExceptionWhenTestIsExpectingPersistenceFeatureButDoesNotHaveDataSourceDefined() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), ConfigurationLoader.createConfiguration("arquillian-without-persistence-properties.xml"));

      // when
      String dataSourceName = metadataProvider.getDataSourceName();

      // then
      // exception should be thrown
   }

   @Test
   public void shouldFetchDataSourceNameFromTest() throws Exception
   {
      // given
      String expectedDataSourceName = DATA_SOURCE_ON_METHOD_LEVEL;
      TestEvent testEvent = new TestEvent(new DataSourceAnnotated(),
            DataSourceAnnotated.class.getMethod("shouldPassWithDataSourceDefinedOnMethodLevel"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      String dataSourceName = metadataProvider.getDataSourceName();

      // then
      assertThat(dataSourceName).isEqualTo(expectedDataSourceName);
   }

   @Test
   public void shouldFetchDataSourceNameFromClassLevelIfNotDefinedForTest() throws Exception
   {
      // given
      String expectedDataSourceName = DATA_SOURCE_ON_CLASS_LEVEL;
      TestEvent testEvent = new TestEvent(new DataSourceAnnotated(),
            DataSourceAnnotated.class.getMethod("shouldPassWithoutDataSourceDefinedOnMethodLevel"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      String dataSourceName = metadataProvider.getDataSourceName();

      // then
      assertThat(dataSourceName).isEqualTo(expectedDataSourceName);
   }

   @Test
   public void shouldFetchDataSourceFromPropertiesWhenNotDefineOnTestOrClassLevel() throws Exception
   {
      // given
      String expectedDataSourceName = "Ike";
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      String dataSourceName = metadataProvider.getDataSourceName();

      // then
      assertThat(dataSourceName).isEqualTo(expectedDataSourceName);
   }

   @Test(expected = DataSourceNotDefinedException.class)
   public void shouldThrowExceptionWhenDataSourceIsNotDefinedInPropertyFileAndClassAndMethod() throws Exception
   {
      // given
      String expectedDataSourceName = "Ike";
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent.getTestMethod(), new MetadataExtractor(testEvent.getTestClass()), ConfigurationLoader.createConfiguration("arquillian-without-persistence-properties.xml"));

      // when
      String dataSourceName = metadataProvider.getDataSourceName();

      // then
      // exception should be thrown
   }

   @DataSource(DATA_SOURCE_ON_CLASS_LEVEL)
   private static class DataSourceAnnotated
   {
      public void shouldPassWithoutDataSourceDefinedOnMethodLevel() {}

      @DataSource(DATA_SOURCE_ON_METHOD_LEVEL)
      public void shouldPassWithDataSourceDefinedOnMethodLevel() {}
   }

   @Data
   private static class DataSourceExpectedFromDefaultConfiguration
   {
      public void shouldPass() {}
   }

}
