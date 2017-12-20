package org.arquillian.ape.rdbms.core;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.yaml.snakeyaml.Yaml;

public class WildflySwarmLoader {

    static final Map<String, String> DEFAULT_DRIVER_NAMES = new HashMap<>();
    public static final String SWARM = "swarm";
    public static final String DATASOURCES = "datasources";
    public static final String DATA_SOURCES = "data-sources";
    public static final String JDBC_DRIVERS = "jdbc-drivers";

    static {
        DEFAULT_DRIVER_NAMES.put("mysql", "com.mysql.jdbc.Driver");
        DEFAULT_DRIVER_NAMES.put("postgresql", "org.postgresql.Driver");
        DEFAULT_DRIVER_NAMES.put("h2", "org.h2.Driver");
        DEFAULT_DRIVER_NAMES.put("edb", "com.edb.Driver");
        DEFAULT_DRIVER_NAMES.put("ibmdb2", "com.ibm.db2.jcc.DB2Driver");
        DEFAULT_DRIVER_NAMES.put("oracle", "oracle.jdbc.OracleDriver");
        DEFAULT_DRIVER_NAMES.put("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        DEFAULT_DRIVER_NAMES.put("sybase", "net.sourceforge.jtds.jdbc.Driver");
        DEFAULT_DRIVER_NAMES.put("teiid", "org.teiid.jdbc.TeiidDriver");
        DEFAULT_DRIVER_NAMES.put("mariadb", "org.mariadb.jdbc.MariaDbDataSource");
        DEFAULT_DRIVER_NAMES.put("derby", "org.apache.derby.jdbc.EmbeddedDriver");
        DEFAULT_DRIVER_NAMES.put("hive2", "org.apache.hive.jdbc.HiveDriver");
        DEFAULT_DRIVER_NAMES.put("prestodb", "com.simba.presto.jdbc42.Driver");
    }

    private static final String JDBC_URI_PROPERTY_NAME = "connection-url";
    private static final String USERNAME_PROPERTY_NAME = "user-name";
    private static final String PASSWORD_PROPERTY_NAME = "password";
    private static final String DRIVER_PROPERTY_NAME = "driver-class-name";
    private static final String DRIVER_NAME = "driver-name";

    public static DatabaseConfiguration load(String name, String location) {

        final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

        final InputStream configurationFile = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(location);

        if (configurationFile == null) {
            throw new IllegalArgumentException(String.format("Wildfly Swarm configuration file %s not found in classpath.", location));
        }

        final Yaml yaml = new Yaml();
        final Map<String, Object> configuration = (Map<String, Object>) yaml.load(configurationFile);

        final Map<String, Object> swarm = (Map<String, Object>) configuration.get(SWARM);

        final Map<String, Object> datasources = Objects.requireNonNull((Map < String, Object >) swarm.get(DATASOURCES),
            String.format("Configuration file %s does not contain %s field", location, DATASOURCES));

        final Map<String, Object> data_sources = Objects.requireNonNull((Map<String, Object>) datasources.get(DATA_SOURCES),
            String.format("Configuration file %s does not contain %s field", location, DATA_SOURCES));

        final Map<String, Object> jdbc_drivers =(Map<String, Object>) datasources.get(JDBC_DRIVERS);

        final String dataSourceName = getDataSourceName(name, data_sources);
        final Map<String, Object> dataSource = (Map<String, Object>) data_sources.get(dataSourceName);

        String driverName = (String) dataSource.get(DRIVER_NAME);
        databaseConfiguration.setJdbcDriver(resolveJdbcDriver(driverName, jdbc_drivers));
        databaseConfiguration.setJdbc(URI.create((String) dataSource.get(JDBC_URI_PROPERTY_NAME)));
        databaseConfiguration.setUsername((String) dataSource.get(USERNAME_PROPERTY_NAME));
        databaseConfiguration.setPassword((String) dataSource.get(PASSWORD_PROPERTY_NAME));

        return databaseConfiguration;
    }

    private static Class<?> resolveJdbcDriver(String driverName, final Map<String, Object> jdbc_drivers) {
        if (jdbc_drivers != null && jdbc_drivers.containsKey(driverName)) {
            final Map<String, Object> jdbcDriver = (Map<String, Object>) jdbc_drivers.get(driverName);
            try {
               return Class.forName((String) jdbcDriver.get(DRIVER_PROPERTY_NAME));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            if (DEFAULT_DRIVER_NAMES.containsKey(driverName)) {
                try {
                    return Class.forName(DEFAULT_DRIVER_NAMES.get(driverName));
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                throw new IllegalArgumentException(String.format("%s driver name could not be resolved to any class driver either in jdbc_drivers nor in default list %s",
                    driverName, DEFAULT_DRIVER_NAMES));
            }
        }
    }

    private static String getDataSourceName(String name, Map<String, Object> data_sources) {
        String dataSourceName = name;

        if (name == null) {
            dataSourceName = data_sources.keySet().iterator().next();
        }
        return dataSourceName;
    }
}
