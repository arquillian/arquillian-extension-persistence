/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.ape.rdbms.dbunit.configuration;

import org.arquillian.ape.rdbms.ShouldMatchDataSet;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.arquillian.ape.rdbms.core.configuration.Configuration;
import org.arquillian.ape.rdbms.core.data.descriptor.Format;
import org.arquillian.ape.rdbms.dbunit.configuration.annotations.Feature;
import org.arquillian.ape.rdbms.dbunit.configuration.annotations.Property;
import org.arquillian.ape.rdbms.util.Arrays;
import org.dbunit.database.CachedResultSetTableFactory;
import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.database.IResultSetTableFactory;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.database.statement.PreparedStatementFactory;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.IColumnFilter;

/**
 * DBUnit configuration which can be customized in <code>arquillian.xml</code>
 * descriptor in the element with qualifier <code>persistence-dbunit</code>.
 * <br><br>
 * Covers all features and properties described in
 * <a href="http://dbunit.sourceforge.net/properties.html">DBUnit documentation</a> as of
 * version 2.5.1
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DBUnitConfiguration extends Configuration {

    private static final long serialVersionUID = 4228916112870401398L;

    @Feature
    private boolean batchedStatements = false;

    @Feature
    private boolean caseSensitiveTableNames = false;

    @Feature
    private boolean qualifiedTableNames = false;

    @Feature
    private boolean datatypeWarning = true;

    @Feature
    private boolean skipOracleRecycleBinTables = false;

    @Feature
    private boolean allowEmptyFields = false;

    @Property
    private String escapePattern;

    @Property
    private String[] tableType;

    @Property
    private IDataTypeFactory datatypeFactory = new DefaultDataTypeFactory();

    @Property
    private IStatementFactory statementFactory = new PreparedStatementFactory();

    @Property
    private IResultSetTableFactory resultSetTableFactory = new CachedResultSetTableFactory();

    @Property
    private IColumnFilter primaryKeyFilter;

    @Property("mssql")
    private IColumnFilter identityColumnFilter;

    @Property
    private int batchSize = 100;

    @Property
    private int fetchSize = 100;

    @Property
    private IMetadataHandler metadataHandler = new DefaultMetadataHandler();

    private boolean useIdentityInsert = false;

    private String defaultDataSetLocation = "datasets/";

    private Format defaultDataSetFormat = Format.XML;

    private boolean excludePoi = false;

    private String[] excludeTablesFromCleanup = new String[0];

    private String[] excludeTablesFromComparisonWhenEmptyExpected = new String[0];

    private String schema;

    private boolean filterTables = false;

    private String customTableFilter;

    public DBUnitConfiguration() {
        super("persistence-dbunit", "arquillian.extension.persistence.dbunit.");
    }

    public boolean isBatchedStatements() {
        return batchedStatements;
    }

    /**
     * @param batchedStatements Enable or disable usage of JDBC batched statement by DBUnit.
     *                          Default value is <code>false</code>
     */
    public void setBatchedStatements(boolean batchedStatements) {
        this.batchedStatements = batchedStatements;
    }

    public boolean isCaseSensitiveTableNames() {
        return caseSensitiveTableNames;
    }

    /**
     * @param caseSensitiveTableNames Enable or disable case sensitive table names.
     *                                If enabled, DBUnit handles all table names in a case sensitive way.
     *                                Default value is <code>false</code>.
     */
    public void setCaseSensitiveTableNames(boolean caseSensitiveTableNames) {
        this.caseSensitiveTableNames = caseSensitiveTableNames;
    }

    public boolean isQualifiedTableNames() {
        return qualifiedTableNames;
    }

    /**
     * @param qualifiedTableNames Enable or disable multiple schemas support. If enabled, DBUnit access
     *                            tables with names fully qualified by schema using this format: SCHEMA.TABLE.
     *                            Default value is <code>false</code>.
     */
    public void setQualifiedTableNames(boolean qualifiedTableNames) {
        this.qualifiedTableNames = qualifiedTableNames;
    }

    public boolean isDatatypeWarning() {
        return datatypeWarning;
    }

    /**
     * @param datatypeWarning Enable or disable the warning message displayed when
     *                        DBUnit encounters an unsupported data type.
     *                        Default value is <code>true</code>.
     */
    public void setDatatypeWarning(boolean datatypeWarning) {
        this.datatypeWarning = datatypeWarning;
    }

    public boolean isSkipOracleRecycleBinTables() {
        return skipOracleRecycleBinTables;
    }

    /**
     * @param skipOracleRecycleBinTables Enable or disable the processing of oracle recycle bin tables (tables starting with BIN$).
     *                                   Oracle 10g recycle bin tables may break DBUnit's assumption of tables name uniqueness within
     *                                   a schema since these table are case sensitive. Enable this feature for Oracle 10g databases
     *                                   until the bug in the oracle driver is fixed, which incorrectly reports this system tables
     *                                   to DBUnit.
     *                                   Default value is <code>false</code>.
     */
    public void setSkipOracleRecycleBinTables(boolean skipOracleRecycleBinTables) {
        this.skipOracleRecycleBinTables = skipOracleRecycleBinTables;
    }

    public boolean isAllowEmptyFields() {
        return allowEmptyFields;
    }

    /**
     * @param allowEmptyFields Allow to call INSERT/UPDATE with empty strings ('').
     *                         Default value is <code>false</code>.
     */
    public void setAllowEmptyFields(boolean allowEmptyFields) {
        this.allowEmptyFields = allowEmptyFields;
    }

    public String getEscapePattern() {
        return escapePattern;
    }

    /**
     * @param escapePattern Allows schema, table and column names escaping. The property value is an escape pattern
     *                      where the ? is replaced by the name. For example, the pattern "[?]" is expanded as "[MY_TABLE]"
     *                      for a table named "MY_TABLE". The most common escape pattern is "\"?\"" which surrounds
     *                      the table name with quotes (for the above example it would result in "\"MY_TABLE\"").
     *                      As a fallback if no questionmark is in the given String and its length is one it is used
     *                      to surround the table name on the left and right side. For example the escape pattern "\""
     *                      will have the same effect as the escape pattern "\"?\"".
     */
    public void setEscapePattern(String escapePattern) {
        this.escapePattern = escapePattern;
    }

    public String[] getTableType() {
        return Arrays.copy(tableType);
    }

    /**
     * @param tableType Used to configure the list of table types recognized by DBUnit.
     * @see java.sql.DatabaseMetaData#getTables for possible values.
     */
    public void setTableType(String[] tableType) {
        this.tableType = Arrays.copy(tableType);
    }

    public IDataTypeFactory getDatatypeFactory() {
        return datatypeFactory;
    }

    /**
     * @param datatypeFactory Used to configure the DataType factory. You can replace the default factory to
     *                        add support for non-standard database vendor data types.
     *                        Default value is {@link DefaultDataTypeFactory}.
     */
    public void setDatatypeFactory(IDataTypeFactory datatypeFactory) {
        this.datatypeFactory = datatypeFactory;
    }

    public IStatementFactory getStatementFactory() {
        return statementFactory;
    }

    /**
     * @param statementFactory Used to configure the statement factory.
     *                         Default value is {@link PreparedStatementFactory}.
     */
    public void setStatementFactory(IStatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public IResultSetTableFactory getResultSetTableFactory() {
        return resultSetTableFactory;
    }

    /**
     * @param resultSetTableFactory Used to configure the ResultSet table factory.
     *                              Default value is {@link CachedResultSetTableFactory}.
     */
    public void setResultSetTableFactory(IResultSetTableFactory resultSetTableFactory) {
        this.resultSetTableFactory = resultSetTableFactory;
    }

    public IColumnFilter getPrimaryKeyFilter() {
        return primaryKeyFilter;
    }

    /**
     * @param primaryKeyFilter Use to override primary keys detection.
     */
    public void setPrimaryKeyFilter(IColumnFilter primaryKeyFilter) {
        this.primaryKeyFilter = primaryKeyFilter;
    }

    public IColumnFilter getIdentityColumnFilter() {
        return identityColumnFilter;
    }

    /**
     * @param identityColumnFilter Used to override IDENTITY column detection (MS SQL specific solution).
     */
    public void setIdentityColumnFilter(IColumnFilter identityColumnFilter) {
        this.identityColumnFilter = identityColumnFilter;
    }

    public int getBatchSize() {
        return batchSize;
    }

    /**
     * @param batchSize The size of batch updates.
     *                  Default value is <code>100</code>.
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * @param fetchSize The statement fetch size for loading data into a result set table.
     *                  Default value is <code>100</code>.
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public IMetadataHandler getMetadataHandler() {
        return metadataHandler;
    }

    /**
     * @param metadataHandler Used to configure the handler used to control database metadata related methods.
     *                        Default value is {@link org.dbunit.database.DefaultMetadataHandler}.
     */
    public void setMetadataHandler(IMetadataHandler metadataHandler) {
        this.metadataHandler = metadataHandler;
    }

    public boolean isUseIdentityInsert() {
        return useIdentityInsert;
    }

    /**
     * @param useIdentityInsert Disables MS SQL Server automatic identifier generation for the execution
     *                          of inserts. For usage with Microsoft driver you should append your JDBC connection with "SelectMethod=cursor".
     *                          Default value is <code>false</code>
     */
    public void setUseIdentityInsert(boolean useIdentityInsert) {
        this.useIdentityInsert = useIdentityInsert;
    }

    public String getDefaultDataSetLocation() {
        return defaultDataSetLocation;
    }

    /**
     * @param defaultDataSetLocation Folder where all datasets are located.
     *                               Default value is <code>datsets<code>.
     */
    public void setDefaultDataSetLocation(String defaultDataSetLocation) {
        this.defaultDataSetLocation = defaultDataSetLocation;
    }

    public Format getDefaultDataSetFormat() {
        return defaultDataSetFormat;
    }

    /**
     * @param defaultDataSetFormat Default format of data sets when file name is inferred from test method name,
     *                             when file is not specified in {@link UsingDataSet} or {@link ShouldMatchDataSet}.
     *                             Default value is {@link Format.XML}
     */
    public void setDefaultDataSetFormat(Format defaultDataSetFormat) {
        this.defaultDataSetFormat = defaultDataSetFormat;
    }

    public boolean isExcludePoi() {
        return excludePoi;
    }

    /**
     * @param excludePoi Excludes Apache POI from packaging process, which results in slimier deployment.
     *                   If you are not using Excel datasets you can safely turn it off.
     *                   Defalut value is <code>false</false>
     */
    public void setExcludePoi(boolean excludePoi) {
        this.excludePoi = excludePoi;
    }

    public String[] getExcludeTablesFromCleanup() {
        return Arrays.copy(excludeTablesFromCleanup);
    }

    /**
     * @param excludeTablesFromCleanup List of tables to be excluded from cleanup procedure.
     *                                 Especially handy for sequence tables which are most likely to be cleared
     *                                 when using STRICT cleanup strategy.
     */
    public void setExcludeTablesFromCleanup(String[] excludeTablesFromCleanup) {
        this.excludeTablesFromCleanup = Arrays.copy(excludeTablesFromCleanup);
    }

    public String getSchema() {
        return schema;
    }

    /**
     * @param schema Schema to be used while creating database connection
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String[] getExcludeTablesFromComparisonWhenEmptyExpected() {
        return excludeTablesFromComparisonWhenEmptyExpected;
    }

    /**
     * @param excludeTablesFromComparisonWhenEmptyExpected list of tables which should not be considered when asserting database content for emptiness.
     */
    public void setExcludeTablesFromComparisonWhenEmptyExpected(String[] excludeTablesFromComparisonWhenEmptyExpected) {
        this.excludeTablesFromComparisonWhenEmptyExpected = excludeTablesFromComparisonWhenEmptyExpected;
    }

    public boolean isFilterTables() {
        return filterTables;
    }

    /**
     * Orders tables using dependency information provided by foreign key metadata. Disabled by default.
     * This property is related to {@link #customTableFilter} which is used to specify which implementation of
     * {@link org.arquillian.persistence.spi.dbunit.filter.TableFilterProvider} should be used.
     * Should be registered using simple name exposed by given implementation {@link org.arquillian.persistence.spi.dbunit.filter.TableFilterProvider}
     *
     * @param filterTables
     */
    public void setFilterTables(boolean filterTables) {
        this.filterTables = filterTables;
    }

    public String getCustomTableFilter() {
        return customTableFilter;
    }

    /**
     * Specifies which implementation of {@link org.arquillian.persistence.spi.dbunit.filter.TableFilterProvider}
     * should be used when {@link #filterTables} is enabled.
     *
     * @param customTableFilter
     */
    public void setCustomTableFilter(String customTableFilter) {
        this.customTableFilter = customTableFilter;
    }
}
