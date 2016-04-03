package org.jboss.arquillian.persistence.dbunit.configuration;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.jcip.annotations.NotThreadSafe;
import org.jboss.arquillian.persistence.core.configuration.ConfigurationImporter;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

@NotThreadSafe
@RunWith(JUnitParamsRunner.class)
public class DBUnitConfigurationPropertyMapperTest
{

   private static final String DBUNIT_FEATURES = "http://www.dbunit.org/features/";

   private static final String DBUNIT_PROPERTIES = "http://www.dbunit.org/properties/";

   private final DBUnitConfiguration configuration = new DBUnitConfiguration();

   private final ConfigurationImporter<DBUnitConfiguration> configurationImporter = new ConfigurationImporter<DBUnitConfiguration>(configuration);

   @Test
   @Parameters(method = "expectedDBUnitProperties")
   public void should_convert_internal_configuration_representation_to_fully_qualified_dbunit_configuration_set(String expectedKey, Object expectedValue) throws Exception
   {
      // given
      configurationImporter.loadFromArquillianXml("arquillian-dbunit.xml");
      DBUnitConfigurationPropertyMapper dbUnitConfigurationPropertyMapper = new DBUnitConfigurationPropertyMapper();

      // when
      Map<String, Object> dbunitProperties = dbUnitConfigurationPropertyMapper.map(configuration);

      // then
      assertThat(dbunitProperties).contains(entry(expectedKey, expectedValue));
   }

   @Test
   public void should_convert_internal_configuration_representation_of_table_type_to_fully_qualified_dbunit_configuration_set() throws Exception
   {
      // given
      configurationImporter.loadFromArquillianXml("arquillian-dbunit.xml");
      DBUnitConfigurationPropertyMapper dbUnitConfigurationPropertyMapper = new DBUnitConfigurationPropertyMapper();

      // when
      Map<String, Object> dbunitProperties = dbUnitConfigurationPropertyMapper.map(configuration);
      String[] actualTableType = (String[]) dbunitProperties.get(DBUNIT_PROPERTIES + "tableType");

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
      assertThat(dbunitProperties).contains(entry(DBUNIT_PROPERTIES + "batchSize", 200));
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
      assertThat(dbunitProperties).contains(entry(DBUNIT_PROPERTIES + "fetchSize", 100));
   }

   @SuppressWarnings("unused") // used by @Parameters
   private Object[] expectedDBUnitProperties()
   {
      return JUnitParamsRunner.$(
            JUnitParamsRunner.$(DBUNIT_FEATURES + "batchedStatements", true),
            JUnitParamsRunner.$(DBUNIT_FEATURES + "caseSensitiveTableNames", true),
            JUnitParamsRunner.$(DBUNIT_FEATURES + "qualifiedTableNames", true),
            JUnitParamsRunner.$(DBUNIT_FEATURES + "datatypeWarning", false),
            JUnitParamsRunner.$(DBUNIT_FEATURES + "skipOracleRecycleBinTables", true),
            JUnitParamsRunner.$(DBUNIT_FEATURES + "allowEmptyFields", true),
            JUnitParamsRunner.$(DBUNIT_PROPERTIES + "escapePattern", "?"),
            JUnitParamsRunner.$(DBUNIT_PROPERTIES + "batchSize", 200),
            JUnitParamsRunner.$(DBUNIT_PROPERTIES + "fetchSize", 300)
      );
   }

}
