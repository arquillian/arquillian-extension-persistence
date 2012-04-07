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
package org.jboss.arquillian.persistence.data.dbunit.configuration;

import org.dbunit.database.CachedResultSetTableFactory;
import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.database.IResultSetTableFactory;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.database.statement.PreparedStatementFactory;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.IColumnFilter;
import org.jboss.arquillian.persistence.configuration.Configuration;
import org.jboss.arquillian.persistence.data.dbunit.configuration.annotations.Feature;
import org.jboss.arquillian.persistence.data.dbunit.configuration.annotations.Property;

/**
 *
 * DBUnit configuration which can be customized in <code>arquillian.xml</code>
 * descriptor in the element with qualifier <code>persistence-dbunit</code>.
 * <br><br>
 * Covers all features and properties described in
 * <a href="http://www.dbunit.org/properties.html">DBUnit documentation</a> as of
 * version 2.4.8
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DBUnitConfiguration extends Configuration
{

   private static final long serialVersionUID = 4228916112870401398L;

   /**
    * Enable or disable usage of JDBC batched statement by DbUnit.
    */
   @Feature
   private boolean batchedStatements = false;

   /**
    * Enable or disable case sensitive table names.
    * If enabled, Dbunit handles all table names in a case sensitive way.
    *
    * Default value: false.
    */
   @Feature
   private boolean caseSensitiveTableNames = false;

   /**
    * Enable or disable multiple schemas support. If enabled, Dbunit access
    * tables with names fully qualified by schema using this format: SCHEMA.TABLE.
    *
    * Default value: false.
    */
   @Feature
   private boolean qualifiedTableNames = false;

   /**
    * Enable or disable the warning message displayed when
    * DbUnit encounter an unsupported data type.
    *
    * Default value: true.
    */
   @Feature
   private boolean datatypeWarning = true;

   /**
    * Enable or disable the processing of oracle recycle bin tables (tables starting with BIN$).
    * Oracle 10g recyle bin tables may break DbUnit's assumption of tables name uniqueness within
    * a schema since these table are case sensitive. Enable this feature for Oracle 10g databases
    * until the bug in the oracle driver is fixed, which incorrectly reports this system tables
    * to DbUnit.
    *
    * Default value: false.
    */
   @Feature
   private boolean skipOracleRecycleBinTables = false;

   /**
    * Allows schema, table and column names escaping. The property value is an escape pattern
    * where the ? is replaced by the name. For example, the pattern "[?]" is expanded as "[MY_TABLE]"
    * for a table named "MY_TABLE". The most common escape pattern is "\"?\"" which surrounds
    * the table name with quotes (for the above example it would result in "\"MY_TABLE\"").
    *
    * As a fallback if no questionmark is in the given String and its length is one it is used
    * to surround the table name on the left and right side. For example the escape pattern "\""
    * will have the same effect as the escape pattern "\"?\"".
    */
   @Property
   private String escapePattern;

   /**
    * Used to configure the list of table types recognized by DbUnit.
    * @see java.sql.DatabaseMetaData#getTables for possible values.
    */
   @Property
   private String[] tableType;

   /**
    * Used to configure the DataType factory. You can replace the default factory to
    * add support for non-standard database vendor data types.
    */
   @Property
   private IDataTypeFactory datatypeFactory = new DefaultDataTypeFactory();

   /**
    * Used to configure the statement factory.
    */
   @Property
   private IStatementFactory statementFactory = new PreparedStatementFactory();

   /**
    * Used to configure the ResultSet table factory.
    */
   @Property
   private IResultSetTableFactory resultSetTableFactory = new CachedResultSetTableFactory();

   /**
    * Use to override primary keys detection.
    */
   @Property
   private IColumnFilter primaryKeyFilter;

   /**
    * Use to override IDENTITY column detection (MS SQL specific solution).
    */
   @Property("mssql")
   private IColumnFilter identityColumnFilter;

   /**
    * Integer object giving the size of batch updates.
    *
    * Default value: 100.
    */
   @Property
   private int batchSize = 100;

   /**
    * Integer object giving the statement fetch size for loading data into a result set table.
    *
    * Default value: 100.
    */
   @Property
   private int fetchSize = 100;

   /**
    * Used to configure the handler used to control database metadata related methods.
    *
    * Default value: org.dbunit.database.DefaultMetadataHandler.
    */
   @Property
   private IMetadataHandler metadataHandler = new DefaultMetadataHandler();

   /**
    * Defines strategy of inserting data to the data store.
    * @see DataSeedStrategy
    */
   private DataSeedStrategy dataSeedStrategy = DataSeedStrategy.INSERT;

   /**
    * Disables MS SQL Server automatic identifier generation for the execution of inserts.
    * For usage with Microsoft driver you should append your jdbc connection with "SelectMethod=cursor".
    */
   private boolean useIdentityInsert;

   public DBUnitConfiguration()
   {
      super("persistence-dbunit", "arquillian.extension.persistence.dbunit.");
   }

   public boolean isBatchedStatements()
   {
      return batchedStatements;
   }

   public void setBatchedStatements(boolean batchedStatements)
   {
      this.batchedStatements = batchedStatements;
   }

   public boolean isCaseSensitiveTableNames()
   {
      return caseSensitiveTableNames;
   }

   public void setCaseSensitiveTableNames(boolean caseSensitiveTableNames)
   {
      this.caseSensitiveTableNames = caseSensitiveTableNames;
   }

   public boolean isQualifiedTableNames()
   {
      return qualifiedTableNames;
   }

   public void setQualifiedTableNames(boolean qualifiedTableNames)
   {
      this.qualifiedTableNames = qualifiedTableNames;
   }

   public boolean isDatatypeWarning()
   {
      return datatypeWarning;
   }

   public void setDatatypeWarning(boolean datatypeWarning)
   {
      this.datatypeWarning = datatypeWarning;
   }

   public boolean isSkipOracleRecycleBinTables()
   {
      return skipOracleRecycleBinTables;
   }

   public void setSkipOracleRecycleBinTables(boolean skipOracleRecycleBinTables)
   {
      this.skipOracleRecycleBinTables = skipOracleRecycleBinTables;
   }

   public String getEscapePattern()
   {
      return escapePattern;
   }

   public void setEscapePattern(String escapePattern)
   {
      this.escapePattern = escapePattern;
   }

   public String[] getTableType()
   {
      return tableType;
   }

   public void setTableType(String[] tableType)
   {
      this.tableType = tableType;
   }

   public IDataTypeFactory getDatatypeFactory()
   {
      return datatypeFactory;
   }

   public void setDatatypeFactory(IDataTypeFactory datatypeFactory)
   {
      this.datatypeFactory = datatypeFactory;
   }

   public IStatementFactory getStatementFactory()
   {
      return statementFactory;
   }

   public void setStatementFactory(IStatementFactory statementFactory)
   {
      this.statementFactory = statementFactory;
   }

   public IResultSetTableFactory getResultSetTableFactory()
   {
      return resultSetTableFactory;
   }

   public void setResultSetTableFactory(IResultSetTableFactory resultSetTableFactory)
   {
      this.resultSetTableFactory = resultSetTableFactory;
   }

   public IColumnFilter getPrimaryKeyFilter()
   {
      return primaryKeyFilter;
   }

   public void setPrimaryKeyFilter(IColumnFilter primaryKeyFilter)
   {
      this.primaryKeyFilter = primaryKeyFilter;
   }

   public IColumnFilter getIdentityColumnFilter()
   {
      return identityColumnFilter;
   }

   public void setIdentityColumnFilter(IColumnFilter identityColumnFilter)
   {
      this.identityColumnFilter = identityColumnFilter;
   }

   public int getBatchSize()
   {
      return batchSize;
   }

   public void setBatchSize(int batchSize)
   {
      this.batchSize = batchSize;
   }

   public int getFetchSize()
   {
      return fetchSize;
   }

   public void setFetchSize(int fetchSize)
   {
      this.fetchSize = fetchSize;
   }

   public IMetadataHandler getMetadataHandler()
   {
      return metadataHandler;
   }

   public void setMetadataHandler(IMetadataHandler metadataHandler)
   {
      this.metadataHandler = metadataHandler;
   }

   public DataSeedStrategy getDataSeedStrategy()
   {
      return dataSeedStrategy;
   }

   public void setDataSeedStrategy(DataSeedStrategy strategy)
   {
      this.dataSeedStrategy = strategy;
   }

   public boolean isUseIdentityInsert()
   {
      return useIdentityInsert;
   }

   public void setUseIdentityInsert(boolean useIdentityInsert)
   {
      this.useIdentityInsert = useIdentityInsert;
   }

}
