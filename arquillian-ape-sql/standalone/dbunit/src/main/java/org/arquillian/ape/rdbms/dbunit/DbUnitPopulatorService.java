package org.arquillian.ape.rdbms.dbunit;

import org.arquillian.ape.core.DataSetLoader;
import org.arquillian.ape.rdbms.core.RdbmsPopulatorService;
import org.arquillian.ape.rdbms.core.dbunit.data.descriptor.Format;
import org.arquillian.ape.rdbms.core.dbunit.dataset.DataSetBuilder;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class DbUnitPopulatorService implements RdbmsPopulatorService<DbUnit> {

    private IDatabaseConnection databaseConnection;

    @Override
    public void connect(URI jdbc, String username, String password, Class<?> driver, Map<String, Object> customOptions) {
        try {
            final JdbcDatabaseTester jdbcDatabaseTester = new JdbcDatabaseTester(driver.getName(), jdbc.toString(), username, password);

            // TODO add custom option to get the schema to use
            //jdbcDatabaseTester.setSchema("");

            this.databaseConnection = jdbcDatabaseTester.getConnection();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (!this.databaseConnection.getConnection().isClosed()) {
                this.databaseConnection.close();
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void execute(List<String> resources) {
        executeDataSetOperation(resources, (connection, dataSet) -> {
            try {
                DatabaseOperation.INSERT.execute(connection, dataSet);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    private void executeDataSetOperation(List<String> resources, BiConsumer<IDatabaseConnection, IDataSet> predicate) {
        resources.stream()
                .map(resource -> {
                    final Format format = Format.inferFromFile(resource);
                    return DataSetBuilder.builderFor(format).build(resource);
                })
                .forEach(dataset -> {
                    predicate.accept(databaseConnection, dataset);
                });
    }

    @Override
    public void clean(List<String> resources) {
        executeDataSetOperation(resources, (connection, dataSet) -> {
            try {
                DatabaseOperation.DELETE.execute(connection, dataSet);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    @Override
    public Class<DbUnit> getPopulatorAnnotation() {
        return DbUnit.class;
    }
}
