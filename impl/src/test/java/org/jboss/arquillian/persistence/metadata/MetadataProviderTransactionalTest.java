package org.jboss.arquillian.persistence.metadata;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class MetadataProviderTransactionalTest
{

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

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
      assertThat(transactionalMode).isEqualTo(TransactionMode.COMMIT);
   }

   @Test
   public void shouldHaveRollbackAsDefaultTransactionalModeWhenNotSpecifiedOnAnyLevel() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new TransactionalAnnotatedClassWithDefaultOnMethodLevel(),
            TransactionalAnnotatedClassWithDefaultOnMethodLevel.class.getMethod("shouldPassWithTransactionalDefaultModeDefinedOnMethodLevel"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      TransactionMode transactionalMode = metadataProvider.getTransactionalMode();

      // then
      assertThat(transactionalMode).isEqualTo(TransactionMode.ROLLBACK);
   }
   
   @Transactional(TransactionMode.COMMIT)
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

}
