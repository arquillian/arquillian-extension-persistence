package org.arquillian.ape.rdbms.dbunit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.database.IResultSetTableFactory;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.IColumnFilter;

public class DbUnitOptions implements Map<String, Object> {

    private static final String FEATURE_PREFIX = "http://www.dbunit.org/features/";
    private static final String PROPERTY_PREFIX = "http://www.dbunit.org/properties/";

    static String SCHEMA = "schema";

    // Features

    static String BATCHED_STATEMENTS = "batchedStatements";
    static String CASE_SENSITIVE_TABLE_NAMES = "caseSensitiveTableNames";
    static String QUALIFIED_TABLE_NAMES = "qualifiedTableNames";
    static String DATATYPE_WARNING = "datatypeWarning";
    static String SKIP_ORACLE_RECYCLE_BIN_TABLES = "skipOracleRecycleBinTables";
    static String ALLOW_EMPTY_FIELDS = "allowEmptyFields";


    // Properties

    static String ESCAPE_PATTERN = "escapePattern";
    static String TABLE_TYPE = "tableType";
    static String DATATYPE_FACTORY = "datatypeFactory";
    static String STATEMENT_FACTORY = "statementFactory";
    static String RESULT_SET_TABLE_FACTORY = "resultSetTableFactory";
    static String PRIMARY_KEY_FILTER = "primaryKeyFilter";
    static String IDENTITY_COLUMN_FILTER = "identityColumnFilter";
    static String BATCH_SIZE = "batchSize";
    static String FETCH_SIZE = "fetchSize";
    static String METADATA_HANDLER = "metadataHandler";

    private Map<String, Object> options = new HashMap<>();

    private DbUnitOptions() {
    }

    DbUnitOptions(Map<String, Object> options) {
        this.options.putAll(options);
    }

    public static DbUnitConfigurationOptions options() {
        return new DbUnitConfigurationOptions();
    }

    @Override
    public int size() {
        return options.size();
    }

    @Override
    public boolean isEmpty() {
        return options.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return options.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return options.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return options.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return options.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return options.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        options.putAll(m);
    }

    @Override
    public void clear() {
        options.clear();
    }

    @Override
    public Set<String> keySet() {
        return options.keySet();
    }

    @Override
    public Collection<Object> values() {
        return options.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return options.entrySet();
    }

    void configure(DatabaseConfig databaseConfig) {

        databaseConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new DefaultDataTypeFactory());
        configureFeatures(databaseConfig);
        configureProperties(databaseConfig);

    }

    private void configureProperties(DatabaseConfig databaseConfig) {
        setProperty(ESCAPE_PATTERN, databaseConfig);
        setProperty(TABLE_TYPE, databaseConfig);
        setProperty(DATATYPE_FACTORY, databaseConfig);
        setProperty(STATEMENT_FACTORY, databaseConfig);
        setProperty(RESULT_SET_TABLE_FACTORY, databaseConfig);
        setProperty(PRIMARY_KEY_FILTER, databaseConfig);
        setIdentityColumnFilter(databaseConfig);
        setProperty(BATCH_SIZE, databaseConfig);
        setProperty(FETCH_SIZE, databaseConfig);
        setProperty(METADATA_HANDLER, databaseConfig);
    }

    private void configureFeatures(DatabaseConfig databaseConfig) {
        setFeature(BATCHED_STATEMENTS, databaseConfig);
        setFeature(CASE_SENSITIVE_TABLE_NAMES, databaseConfig);
        setFeature(QUALIFIED_TABLE_NAMES, databaseConfig);
        setFeature(DATATYPE_WARNING, databaseConfig);
        setFeature(SKIP_ORACLE_RECYCLE_BIN_TABLES, databaseConfig);
        setFeature(ALLOW_EMPTY_FIELDS, databaseConfig);
    }

    private void setIdentityColumnFilter(DatabaseConfig databaseConfig) {
        if (this.options.containsKey(IDENTITY_COLUMN_FILTER)) {
            String key = PROPERTY_PREFIX +"mssql/" + IDENTITY_COLUMN_FILTER;
            databaseConfig.setProperty(key, this.options.get(IDENTITY_COLUMN_FILTER));
        }
    }

    private void setProperty(String property, DatabaseConfig databaseConfig) {
        if (this.options.containsKey(property)) {
            String key = PROPERTY_PREFIX + property;
            databaseConfig.setProperty(key, this.options.get(property));
        }
    }

    private void setFeature(String property, DatabaseConfig databaseConfig) {
        if (this.options.containsKey(property)) {
            String key = FEATURE_PREFIX + property;
            databaseConfig.setProperty(key, this.options.get(property));
        }
    }

    public static class DbUnitConfigurationOptions {

        private DbUnitOptions dbUnitOptions = new DbUnitOptions();

        private DbUnitConfigurationOptions() {
        }

        public DbUnitConfigurationOptions schema(String schema) {
            dbUnitOptions.put(DbUnitOptions.SCHEMA, schema);

            return this;
        }

        public DbUnitConfigurationOptions batchedStatements(boolean batchedStatements) {
            dbUnitOptions.put(DbUnitOptions.BATCHED_STATEMENTS, batchedStatements);

            return this;
        }

        public DbUnitConfigurationOptions caseSensitiveTableNames(boolean caseSensitiveTableNames) {
            dbUnitOptions.put(DbUnitOptions.CASE_SENSITIVE_TABLE_NAMES, caseSensitiveTableNames);

            return this;
        }

        public DbUnitConfigurationOptions qualifiedTableNames(boolean qualifiedTableNames) {
            dbUnitOptions.put(DbUnitOptions.QUALIFIED_TABLE_NAMES, qualifiedTableNames);

            return this;
        }

        public DbUnitConfigurationOptions datatypeWarning(boolean datatypeWarning) {
            dbUnitOptions.put(DbUnitOptions.DATATYPE_WARNING, datatypeWarning);

            return this;
        }

        public DbUnitConfigurationOptions skipOracleRecycleBinTables(boolean skipOracleRecycleBinTables) {
            dbUnitOptions.put(DbUnitOptions.SKIP_ORACLE_RECYCLE_BIN_TABLES, skipOracleRecycleBinTables);

            return this;
        }

        public DbUnitConfigurationOptions allowEmptyFields(boolean allowEmptyFields) {
            dbUnitOptions.put(DbUnitOptions.ALLOW_EMPTY_FIELDS, allowEmptyFields);

            return this;
        }

        public DbUnitConfigurationOptions escapePattern(String escapePattern) {
            dbUnitOptions.put(DbUnitOptions.ESCAPE_PATTERN, escapePattern);

            return this;
        }

        public DbUnitConfigurationOptions tableType(String[] tableType) {
            dbUnitOptions.put(DbUnitOptions.TABLE_TYPE, tableType);

            return this;
        }

        public DbUnitConfigurationOptions datatypeFactory(IDataTypeFactory iDataTypeFactory) {
            dbUnitOptions.put(DbUnitOptions.DATATYPE_FACTORY, iDataTypeFactory);

            return this;
        }

        public DbUnitConfigurationOptions statementFactory(IStatementFactory iStatementFactory) {
            dbUnitOptions.put(DbUnitOptions.STATEMENT_FACTORY, iStatementFactory);

            return this;
        }

        public DbUnitConfigurationOptions resultSetTableFactory(IResultSetTableFactory iResultSetTableFactory) {
            dbUnitOptions.put(DbUnitOptions.RESULT_SET_TABLE_FACTORY, iResultSetTableFactory);

            return this;
        }

        public DbUnitConfigurationOptions primaryKeyFilter(IColumnFilter iColumnFilter) {
            dbUnitOptions.put(DbUnitOptions.PRIMARY_KEY_FILTER, iColumnFilter);

            return this;
        }

        public DbUnitConfigurationOptions identityColumnFilter(IColumnFilter iColumnFilter) {
            dbUnitOptions.put(DbUnitOptions.IDENTITY_COLUMN_FILTER, iColumnFilter);

            return this;
        }

        public DbUnitConfigurationOptions batchSize(int batchSize) {
            dbUnitOptions.put(DbUnitOptions.BATCH_SIZE, batchSize);

            return this;
        }

        public DbUnitConfigurationOptions fetchSize(int fetchSize) {
            dbUnitOptions.put(DbUnitOptions.FETCH_SIZE, fetchSize);

            return this;
        }

        public DbUnitConfigurationOptions metadataHandler(IMetadataHandler metadataHandler) {
            dbUnitOptions.put(DbUnitOptions.METADATA_HANDLER, metadataHandler);

            return this;
        }

        public DbUnitOptions build() {
            return dbUnitOptions;
        }

    }

}
