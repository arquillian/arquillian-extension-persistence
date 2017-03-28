package org.arquillian.ape.rdbms.dbunit.configuration;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.jcip.annotations.NotThreadSafe;
import org.arquillian.ape.rdbms.core.configuration.ConfigurationImporter;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.statement.StatementFactory;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.netezza.NetezzaMetadataHandler;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@NotThreadSafe
@RunWith(JUnitParamsRunner.class)
public class DBUnitConfigurationPropertyMapperComparedByTypeTest {
    private final DBUnitConfiguration configuration = new DBUnitConfiguration();

    private final ConfigurationImporter<DBUnitConfiguration> configurationImporter = new ConfigurationImporter<DBUnitConfiguration>(configuration);

    @Test
    @Parameters(method = "expectedDbunitPropertiesToBeComparedByType")
    public void should_convert_internal_configuration_representation_to_fully_qualified_dbunit_configuration_set(String expectedKey, Class expectedType) throws Exception {
        // given
        configurationImporter.loadFromArquillianXml("arquillian-dbunit.xml");
        DBUnitConfigurationPropertyMapper dbUnitConfigurationPropertyMapper = new DBUnitConfigurationPropertyMapper();

        // when
        Map<String, Object> dbunitProperties = dbUnitConfigurationPropertyMapper.map(configuration);

        // then
        assertThat(dbunitProperties.get(expectedKey)).isInstanceOf(expectedType);

    }

    @SuppressWarnings("unused") // used by @Parameters
    private Object[] expectedDbunitPropertiesToBeComparedByType() {
        return JUnitParamsRunner.$(
                JUnitParamsRunner.$("http://www.dbunit.org/properties/datatypeFactory", HsqldbDataTypeFactory.class),
                JUnitParamsRunner.$("http://www.dbunit.org/properties/statementFactory", StatementFactory.class),
                JUnitParamsRunner.$("http://www.dbunit.org/properties/resultSetTableFactory", ForwardOnlyResultSetTableFactory.class),
                JUnitParamsRunner.$("http://www.dbunit.org/properties/primaryKeyFilter", DefaultColumnFilter.class),
                JUnitParamsRunner.$("http://www.dbunit.org/properties/mssql/identityColumnFilter", DefaultColumnFilter.class),
                JUnitParamsRunner.$("http://www.dbunit.org/properties/metadataHandler", NetezzaMetadataHandler.class)
        );
    }
}
