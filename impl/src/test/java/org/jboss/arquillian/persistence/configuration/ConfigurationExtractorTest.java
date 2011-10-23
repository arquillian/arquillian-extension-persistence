package org.jboss.arquillian.persistence.configuration;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.data.Format;
import org.junit.Test;

public class ConfigurationExtractorTest
{

   @Test
   public void shouldExtractDefaultDataSourceFromExternalConfigurationFile() throws Exception
   {
      // given
      String expectedDataSource = "Ike";
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractorForDefaultConfiguration();

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
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractorForDefaultConfiguration();

      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getInitStatement()).isEqualTo(expectedInitStatement);
   }
   
   @Test
   public void shouldExtractDefaultDataSetFormatDefinedInPropertyFile() throws Exception
   {
      // given
      Format expectedFormat = Format.EXCEL;
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractorForDefaultConfiguration();
      
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
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractor("arquillian-without-persistence-properties.xml");
      
      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getDefaultDataSetFormat()).isEqualTo(expectedFormat);
   }
   
   @Test
   public void shouldObtainDefaultTransactionMode() throws Exception
   {
      // given
      TransactionMode expectedMode = TransactionMode.ROLLBACK;
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractor("arquillian.xml");
      
      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getDefaultTransactionMode()).isEqualTo(expectedMode);
   }
   
   @Test
   public void shouldHaveCommitAsDefaultTransactionModeIfNotDefinedInConfigurationFile() throws Exception
   {
      // given
      TransactionMode expectedMode = TransactionMode.COMMIT;
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractor("arquillian-without-persistence-properties.xml");
      
      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getDefaultTransactionMode()).isEqualTo(expectedMode);
   }
   
   @Test
   public void shouldBeAbleToTurnOnDatabaseDumps() throws Exception
   {
      // given
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractor("arquillian.xml");
      
      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.isDumpData()).isTrue();
   }
   
   @Test
   public void shouldHaveDatabaseDumpsDisbaledByDefault() throws Exception
   {
      // given
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractor("arquillian-without-persistence-properties.xml");
      
      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.isDumpData()).isFalse();
   }
   
   public void shouldHaveSystemTempDirDefinedAsDefaultDumpDirectory() throws Exception
   {
      // given
      String systemTmpDir = System.getProperty("java.io.tmpdir");
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractor("arquillian-without-persistence-properties.xml");
      
      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getDumpDirectory()).isEqualTo(systemTmpDir);
   }
   
   @Test
   public void shouldBeAbleToDefineDumpDirectory() throws Exception
   {
      // given
      String dumpDirectory = "/home/ike/dump";
      ConfigurationExtractor configurationExtractor = ConfigurationLoader.createConfigurationExtractor("arquillian.xml");

      // when
      PersistenceConfiguration configuration = configurationExtractor.extract();

      // then
      assertThat(configuration.getDumpDirectory()).isEqualTo(dumpDirectory);
   }
}
