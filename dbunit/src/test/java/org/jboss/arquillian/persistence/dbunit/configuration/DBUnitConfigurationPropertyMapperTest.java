package org.jboss.arquillian.persistence.dbunit.configuration;

import static junitparams.JUnitParamsRunner.$;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.jboss.arquillian.persistence.core.configuration.ConfigurationImporter;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class DBUnitConfigurationPropertyMapperTest
{
   private final DBUnitConfiguration configuration = new DBUnitConfiguration();

   private final ConfigurationImporter<DBUnitConfiguration> configurationImporter = new ConfigurationImporter<DBUnitConfiguration>(configuration);

   @Test
   @Parameters(method = "expectedDbunitProperties")
   public void should_convert_internal_configuration_representation_to_fully_qualified_dbunit_configuration_set(String expectedKey, Object expectedValue) throws Exception
   {
      // given
      configurationImporter.loadFromArquillianXml("arquillian-dbunit.xml");
      DBUnitConfigurationPropertyMapper dbUnitConfigurationPropertyMapper = new DBUnitConfigurationPropertyMapper();

      // when
      Map<String, Object> dbunitProperties = dbUnitConfigurationPropertyMapper.map(configuration);

      // then
      assertThat(dbunitProperties).includes(entry(expectedKey, expectedValue));
   }

   @Test
   public void should_convert_internal_configuration_representation_of_table_type_to_fully_qualified_dbunit_configuration_set() throws Exception
   {
      // given
      configurationImporter.loadFromArquillianXml("arquillian-dbunit.xml");
      DBUnitConfigurationPropertyMapper dbUnitConfigurationPropertyMapper = new DBUnitConfigurationPropertyMapper();

      // when
      Map<String, Object> dbunitProperties = dbUnitConfigurationPropertyMapper.map(configuration);
      String[] actualTableType = (String[]) dbunitProperties.get("http://www.dbunit.org/properties/tableType");

      // then
      assertThat(actualTableType).containsOnly("TABLE", "VIEW");
   }

   @Test
   public void should_convert_non_null_values_only() throws Exception
   {
      // given
      configurationImporter.loadFromArquillianXml("arquillian-dbunit-batchsize-only.xml");
      DBUnitConfigurationPropertyMapper dbUnitConfigurationPropertyMapper = new DBUnitConfigurationPropertyMapper();

      // when
      Map<String, Object> dbunitProperties = dbUnitConfigurationPropertyMapper.map(configuration);

      // then
      assertThat(dbunitProperties).includes(entry("http://www.dbunit.org/properties/batchSize", 200));
   }

   @Test
   public void should_not_overwrite_when_null_specified() throws Exception
   {
      // given
      configurationImporter.loadFromArquillianXml("arquillian-dbunit-batchsize-only.xml");
      DBUnitConfigurationPropertyMapper dbUnitConfigurationPropertyMapper = new DBUnitConfigurationPropertyMapper();

      // when
      Map<String, Object> dbunitProperties = dbUnitConfigurationPropertyMapper.map(configuration);

      // then
      assertThat(dbunitProperties).includes(entry("http://www.dbunit.org/properties/fetchSize", 100));
   }

   @SuppressWarnings("unused") // used by @Parameters
   private Object[] expectedDbunitProperties()
   {
      return JUnitParamsRunner.$(
            JUnitParamsRunner.$("http://www.dbunit.org/features/batchedStatements", true),
            JUnitParamsRunner.$("http://www.dbunit.org/features/caseSensitiveTableNames", true),
            JUnitParamsRunner.$("http://www.dbunit.org/features/qualifiedTableNames", true),
            JUnitParamsRunner.$("http://www.dbunit.org/features/datatypeWarning", false),
            JUnitParamsRunner.$("http://www.dbunit.org/features/skipOracleRecycleBinTables", true),
            JUnitParamsRunner.$("http://www.dbunit.org/properties/escapePattern", "?"),
            JUnitParamsRunner.$("http://www.dbunit.org/properties/batchSize", 200),
            JUnitParamsRunner.$("http://www.dbunit.org/properties/fetchSize", 300)
      );
   }

}
