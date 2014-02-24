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
package org.jboss.arquillian.persistence.dbunit.configuration;

import org.jboss.arquillian.persistence.core.data.descriptor.Format;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public abstract class DBUnitConfigurationImporterFromFileAbstractTestCase
{


   protected abstract DBUnitConfiguration loadFromFile() throws IOException;

   // -- Test cases

   @Test
   public void should_extract_batched_statement_flag_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.isBatchedStatements()).isTrue();
   }

   @Test
   public void should_extract_case_sensitive_table_names_flag_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.isCaseSensitiveTableNames()).isTrue();
   }

   @Test
   public void should_extract_qualified_table_names_flag_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.isQualifiedTableNames()).isTrue();
   }

   @Test
   public void should_extract_data_type_warning_flag_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.isDatatypeWarning()).isFalse();
   }

   @Test
   public void should_extract_skip_oracle_recycle_bin_tables_flag_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.isSkipOracleRecycleBinTables()).isTrue();
   }

   @Test
   public void should_extract_escape_pattern_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getEscapePattern()).isEqualTo("?");
   }

   @Test
   public void should_extract_table_types_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getTableType()).contains("TABLE", "VIEW");
   }

   @Test
   public void should_extract_datatype_factory_implementation_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getDatatypeFactory()).isInstanceOf(org.dbunit.ext.hsqldb.HsqldbDataTypeFactory.class);
   }

   @Test
   public void should_extract_statement_factory_implementation_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getStatementFactory()).isInstanceOf(org.dbunit.database.statement.StatementFactory.class);
   }

   @Test
   public void should_extract_result_set_table_factory_factory_implementation_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getResultSetTableFactory()).isInstanceOf(org.dbunit.database.ForwardOnlyResultSetTableFactory.class);
   }

   @Test
   public void should_extract_primary_key_filter_implementation_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getPrimaryKeyFilter()).isInstanceOf(org.dbunit.dataset.filter.DefaultColumnFilter.class);
   }

   @Test
   public void should_extract_identity_column_filter_implementation_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getIdentityColumnFilter()).isInstanceOf(org.dbunit.dataset.filter.DefaultColumnFilter.class);
   }

   @Test
   public void should_extract_metadata_handler_implementation_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getMetadataHandler()).isInstanceOf(org.dbunit.ext.netezza.NetezzaMetadataHandler.class);
   }

   @Test
   public void should_extract_batch_size_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getBatchSize()).isEqualTo(200);
   }

   @Test
   public void should_extract_fetch_size_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getFetchSize()).isEqualTo(300);
   }

   @Test
   public void should_extract_identity_insert_flag_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.isUseIdentityInsert()).isTrue();
   }

   @Test
   public void should_extract_default_data_set_format_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getDefaultDataSetFormat()).isEqualTo(Format.EXCEL);
   }

   @Test
   public void should_extract_default_data_set_location_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getDefaultDataSetLocation()).isEqualTo("ds");
   }

   @Test
   public void should_extract_schema_name_from_external_property_file() throws Exception
   {
      DBUnitConfiguration dbunitConfiguration = loadFromFile();

      // then
      assertThat(dbunitConfiguration.getSchema()).isEqualTo("ape");
   }

}
