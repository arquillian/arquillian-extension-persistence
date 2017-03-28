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

import org.jboss.arquillian.persistence.testutils.TestConfigurationLoader;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceConfigurationImporterFromPropertyFileTest {

    @Test
    public void should_extract_default_data_source_from_external_configuration_file() throws Exception {
        // given
        String expectedDataSource = "Ike";
        Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(properties);

        // then
        assertThat(configuration.getDefaultDataSource()).isEqualTo(expectedDataSource);
    }

    @Test
    public void should_obtain_default_transaction_mode() throws Exception {
        // given
        TransactionMode expectedMode = TransactionMode.ROLLBACK;
        Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();

        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(properties);

        // then
        assertThat(configuration.getDefaultTransactionMode()).isEqualTo(expectedMode);
    }

    @Test
    public void should_be_able_to_turn_on_database_dumps() throws Exception {
        // given
        Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(properties);

        // then
        assertThat(configuration.isDumpData()).isTrue();
    }

    @Test
    public void should_be_able_to_define_dump_directory() throws Exception {
        // given
        String dumpDirectory = "/home/ike/dump";
        Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(properties);

        // then
        assertThat(configuration.getDumpDirectory()).isEqualTo(dumpDirectory);
    }

    @Test
    public void should_be_able_to_define_dump_directory_windows() throws Exception {
        // given
        String dumpDirectory = "C:\\Users\\Arq\\AppData\\Local\\Temp";
        Properties properties = TestConfigurationLoader.createPropertiesFrom("properties/custom.arquillian.persistence.windows.dump.properties");
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(properties);

        // then
        assertThat(configuration.getDumpDirectory()).isEqualTo(dumpDirectory);
    }

    @Test
    public void should_ignore_unknown_property() throws Exception {
        // given
        final Properties properties = TestConfigurationLoader.createPropertiesFrom("properties/arquillian.persistence.with.unhandled.entries.properties");
        final PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(properties);

        // then
        assertThat(configuration.isDumpData()).isFalse();
    }
}
