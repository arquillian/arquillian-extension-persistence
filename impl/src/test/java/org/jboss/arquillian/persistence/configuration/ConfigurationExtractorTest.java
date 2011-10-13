package org.jboss.arquillian.persistence.configuration;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.Format;
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
   
   @Test
   public void shouldExtractDefaultDataSetFormatDefinedInPropertyFile() throws Exception
   {
      // given
      Format expectedFormat = Format.XLS;
      ConfigurationExtractor configurationExtractor = TestConfigurationLoader.createConfigurationExtractorForDefaultConfiguration();
      
      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getDefaultDataSetFormat()).isEqualTo(expectedFormat);
   }
   
   @Test
   public void shouldUseXmlAsDefaultDataSetFormatWhenNotDefinedInConfiguration() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      ConfigurationExtractor configurationExtractor = TestConfigurationLoader.createConfigurationExtractor("arquillian-without-persistence-properties.xml");
      
      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getDefaultDataSetFormat()).isEqualTo(expectedFormat);
   }

}
