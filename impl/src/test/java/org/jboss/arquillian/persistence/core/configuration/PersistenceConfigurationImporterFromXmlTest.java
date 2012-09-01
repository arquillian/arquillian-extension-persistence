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
package org.jboss.arquillian.persistence.core.configuration;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.junit.Test;

public class PersistenceConfigurationImporterFromXmlTest
{

   @Test
   public void should_extract_default_data_source_from_external_configuration_file() throws Exception
   {
      // given
      String expectedDataSource = "Ike";
      ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptorFromDefaultConfigurationFile();
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(descriptor);

      // then
      assertThat(configuration.getDefaultDataSource()).isEqualTo(expectedDataSource);
   }

   @Test
   public void should_extract_init_statement_from_external_configuration_file() throws Exception
   {
      // given
      String expectedInitStatement = "SELECT * FROM ARQUILLIAN_TESTS";
      ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptorFromDefaultConfigurationFile();
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(descriptor);

      // then
      assertThat(configuration.getScriptsToExecuteBeforeTest()).containsOnly(expectedInitStatement);
   }

   @Test
   public void should_be_able_to_turn_on_database_dumps() throws Exception
   {
      // given
      ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian.xml");
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(descriptor);

      // then
      assertThat(configuration.isDumpData()).isTrue();
   }

   @Test
   public void should_have_database_dumps_disabled_by_default() throws Exception
   {
      // given
      ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian-without-persistence-properties.xml");
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(descriptor);

      // then
      assertThat(configuration.isDumpData()).isFalse();
   }

   public void should_have_system_temp_dir_defined_as_default_dump_directory() throws Exception
   {
      // given
      String systemTmpDir = System.getProperty("java.io.tmpdir");
      ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian-without-persistence-properties.xml");
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(descriptor);

      // then
      assertThat(configuration.getDumpDirectory()).isEqualTo(systemTmpDir);
   }

   @Test
   public void should_be_able_to_define_dump_directory() throws Exception
   {
      // given
      String dumpDirectory = "/home/ike/dump";
      ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian.xml");
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(descriptor);

      // then
      assertThat(configuration.getDumpDirectory()).isEqualTo(dumpDirectory);
   }
}
