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
package org.arquillian.ape.rdbms.core.configuration;

import org.arquillian.ape.rdbms.testutils.TestConfigurationLoader;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceConfigurationImporterFromXmlTest {

    @Test
    public void should_extract_default_data_source_from_external_configuration_file() throws Exception {
        // given
        String expectedDataSource = "Ike";
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptorFromDefaultConfigurationFile();
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.getDefaultDataSource()).isEqualTo(expectedDataSource);
    }

    @Test
    public void should_obtain_default_transaction_mode() throws Exception {
        // given
        TransactionMode expectedMode = TransactionMode.ROLLBACK;
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian.xml");
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.getDefaultTransactionMode()).isEqualTo(expectedMode);
    }

    @Test
    public void should_have_commit_as_default_transaction_mode_if_not_defined_in_configuration_file() throws Exception {
        // given
        TransactionMode expectedMode = TransactionMode.COMMIT;
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian-without-persistence-properties.xml");
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.getDefaultTransactionMode()).isEqualTo(expectedMode);
    }

    @Test
    public void should_be_able_to_turn_on_database_dumps() throws Exception {
        // given
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian.xml");
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.isDumpData()).isTrue();
    }

    @Test
    public void should_have_database_dumps_disabled_by_default() throws Exception {
        // given
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian-without-persistence-properties.xml");
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.isDumpData()).isFalse();
    }

    @Test
    public void should_have_system_temp_dir_defined_as_default_dump_directory() throws Exception {
        // given
        String systemTmpDir = System.getProperty("java.io.tmpdir");
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian-without-persistence-properties.xml");
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.getDumpDirectory()).isEqualTo(systemTmpDir);
    }

    @Test
    public void should_be_able_to_define_dump_directory() throws Exception {
        // given
        String dumpDirectory = "/home/ike/dump";
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian.xml");
        PersistenceConfiguration configuration = new PersistenceConfiguration();

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.getDumpDirectory()).isEqualTo(dumpDirectory);
    }

}
