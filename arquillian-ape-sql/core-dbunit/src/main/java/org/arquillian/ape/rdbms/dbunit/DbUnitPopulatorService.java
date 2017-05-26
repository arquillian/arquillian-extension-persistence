package org.arquillian.ape.rdbms.dbunit;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.arquillian.ape.rdbms.core.RdbmsPopulatorService;
import org.arquillian.ape.rdbms.core.dbunit.data.descriptor.Format;
import org.arquillian.ape.rdbms.core.dbunit.dataset.DataSetBuilder;
import org.dbunit.DatabaseUnitException;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

public class DbUnitPopulatorService implements RdbmsPopulatorService<DbUnit> {

    private static final Logger log = Logger.getLogger(DbUnitPopulatorService.class.getName());

    private IDatabaseConnection databaseConnection;

    @Override
    public void connect(URI jdbc, String username, String password, Class<?> driver, Map<String, Object> customOptions) {
        try {
            this.databaseConnection = lookupDataSourceConnection(jdbc.toString(), customOptions)
                .orElseGet(() -> connectUsingJdbc(driver.getName(), jdbc.toString(), username, password, customOptions));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private IDatabaseConnection connectUsingJdbc(String driverName, String jdbcUri, String username, String password, Map<String, Object> customOptions)  {
        try {
            final JdbcDatabaseTester jdbcDatabaseTester =
                new JdbcDatabaseTester(driverName, jdbcUri, username, password);

            if (customOptions.containsKey(DbUnitOptions.SCHEMA)) {
                   jdbcDatabaseTester.setSchema((String) customOptions.get(DbUnitOptions.SCHEMA));
            }

            final IDatabaseConnection connection = jdbcDatabaseTester.getConnection();
            DbUnitOptions dbUnitOptions = new DbUnitOptions(customOptions);
            dbUnitOptions.configure(connection.getConfig());

            return connection;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (this.databaseConnection != null && !this.databaseConnection.getConnection().isClosed()) {
                this.databaseConnection.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
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
            .forEach(dataset -> predicate.accept(databaseConnection, dataset));
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

    // Temporary solution

    @Inject
    private Instance<Context> contextInstance;

    private Optional<IDatabaseConnection> lookupDataSourceConnection(String dataSourceName,
        Map<String, Object> customOptions) {

        try {

            if (contextInstance == null) {
                // In case of running outside Arquillian Runner
                return Optional.empty();
            }
            final Context context = contextInstance.get();
            if (context == null) {
                return Optional.empty();
            }
            final Connection connection = ((DataSource) context.lookup(dataSourceName)).getConnection();

            final DatabaseConnection databaseConnection = new DatabaseConnection(connection);

            if (customOptions.size() > 0) {
                DbUnitOptions dbUnitOptions = new DbUnitOptions(customOptions);
                dbUnitOptions.configure(databaseConnection.getConfig());
            }

            return Optional.of(databaseConnection);
        } catch (NamingException | SQLException | DatabaseUnitException e) {
            log.warning("Failed performing datasource [" + dataSourceName + "] lookup. Cause: " + e.getMessage());
            return Optional.empty();
        }
    }
}
