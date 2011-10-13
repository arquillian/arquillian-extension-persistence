package org.jboss.arquillian.persistence.metadata;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.configuration.ConfigurationLoader;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class MetadataProviderPersistentFeatureTest
{

   @Test(expected = DataSourceNotDefinedException.class)
   public void shouldThrownExceptionWhenTestIsExpectingPersistenceFeatureButDoesNotHaveDataSourceDefined() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, ConfigurationLoader.createConfiguration("arquillian-without-persistence-properties.xml"));

      // when
      boolean persistenceFeatureEnabled = metadataProvider.isPersistenceFeatureEnabled();

      // then
      // exception should be thrown
      
   }
   
   @Test
   public void shouldNotAcceptClassWithoutDataAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new NonPersistenceTest(),
            NonPersistenceTest.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, ConfigurationLoader.createDefaultConfiguration());

      // when
      boolean persistenceFeatureEnabled = metadataProvider.isPersistenceFeatureEnabled();

      // then
      assertThat(persistenceFeatureEnabled).isFalse();
   }
   
   @Test
   public void shoulAcceptClassWithoutDataSourceAnnotationButDefinedInProperties() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DataSourceExpectedFromDefaultConfiguration(),
            DataSourceExpectedFromDefaultConfiguration.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, ConfigurationLoader.createDefaultConfiguration());

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
   
   private static class NonPersistenceTest
   {
      public void shouldPass() {}
   }

   
}
