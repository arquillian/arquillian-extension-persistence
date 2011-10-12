package org.jboss.arquillian.persistence.configuration;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class ConfigurationExtractorTest
{

   @Test
   public void shouldExtractDefaultDataSourceFromExternalConfigurationFile() throws Exception
   {
      // given
      String expectedDataSource = "Ike";
      ConfigurationExtractor configurationExtractor = TestConfigurationLoader.createConfigurationExtractorForDefaultConfiguration();

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
      ConfigurationExtractor configurationExtractor = TestConfigurationLoader.createConfigurationExtractorForDefaultConfiguration();

      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getInitStatement()).isEqualTo(expectedInitStatement);
   }

}
