package org.jboss.arquillian.persistence.configuration;

import static org.fest.assertions.Assertions.assertThat;

import java.io.InputStream;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.persistence.configuration.ConfigurationExtractor;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.junit.Test;

public class ConfigurationExtractorTest
{

   private static final String CONFIG_FILENAME = "arquillian.xml";

   @Test
   public void shouldExtractDefaultDataSourceFromExternalConfigurationFile() throws Exception
   {
      // given
      String expectedDataSource = "Ike";
      ArquillianDescriptor descriptor = Descriptors.importAs(ArquillianDescriptor.class)
                                                   .from(loadArquillianConfiguration());

      ConfigurationExtractor configurationExtractor = new ConfigurationExtractor(descriptor);

      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getDefaultDataSource()).isEqualTo(expectedDataSource);
   }
   
   @Test
   public void shouldExtractInitStatementFromExternalConfigurationFile() throws Exception
   {
      // given
      String expectedInitStatement = "SELECT * FROM ARQUILLIAN_TESTS";
      ArquillianDescriptor descriptor = Descriptors.importAs(ArquillianDescriptor.class)
                                                   .from(loadArquillianConfiguration());

      ConfigurationExtractor configurationExtractor = new ConfigurationExtractor(descriptor);

      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getInitStatement()).isEqualTo(expectedInitStatement);
   }

   // -------
   // Utility methods

   private InputStream loadArquillianConfiguration()
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      return classLoader.getResourceAsStream(CONFIG_FILENAME);
   }

}
