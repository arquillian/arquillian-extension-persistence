package org.jboss.arquillian.populator.sql.core;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.populator.core.DataSetLoader;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class SqlPopulatorService implements org.jboss.arquillian.populator.nosql.api.SqlPopulatorService<Sql> {

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
                    String type = resource.substring(resource.lastIndexOf('.'));
                    switch (type) {
                        case "xml": {
                            return resolveXmlDataSet(resource);
                        }

                        default:
                            return resolveXmlDataSet(resource);
                    }
                })
                .forEach(dataset -> {
                    predicate.accept(databaseConnection, dataset);
                });
    }

    private IDataSet resolveXmlDataSet(String resource) {
        try {
            return new FlatXmlDataSetBuilder().build(DataSetLoader.resolve(resource));
        } catch (DataSetException e) {
            throw new IllegalArgumentException(e);
        }
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
    public Class<Sql> getPopulatorAnnotation() {
        return Sql.class;
    }
}
