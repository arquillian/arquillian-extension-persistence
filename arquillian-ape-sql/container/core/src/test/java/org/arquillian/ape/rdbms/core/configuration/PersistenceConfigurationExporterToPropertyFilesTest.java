/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceConfigurationExporterToPropertyFilesTest {

    private File tmpPropertyFile;

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Before
    public void createTemporaryFile() throws IOException {
        this.tmpPropertyFile = tmp.newFile();
    }

    @Test
    public void should_export_persistence_configuration_to_property_file() throws Exception {
        // given
        Properties expectedProperties = expectedProperties("properties/basic.arquillian.persistence.properties");
        expectedProperties.setProperty("arquillian.extension.persistence.dump.directory", System.getProperty("java.io.tmpdir"));

        PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
        persistenceConfiguration.setDefaultDataSource("DefaultDS");
        persistenceConfiguration.setDefaultTransactionMode(TransactionMode.ROLLBACK);

        // when
        Configuration.exportUsing(persistenceConfiguration)
                .toProperties(new FileOutputStream(tmpPropertyFile));

        // then
        assertThat(createdProperties()).isEqualTo(expectedProperties);
    }

    @Test
    public void should_export_custom_persistence_configuration_loaded_from_xml_to_property_file() throws Exception {
        // given
        Properties expectedProperties = expectedProperties("properties/custom.arquillian.persistence.properties");
        PersistenceConfiguration persistenceConfiguration = TestConfigurationLoader.createPersistenceConfigurationFrom("arquillian.xml");

        // when
        Configuration.exportUsing(persistenceConfiguration)
                .toProperties(new FileOutputStream(tmpPropertyFile));

        // then
        assertThat(createdProperties()).isEqualTo(expectedProperties);
    }

    // Utility methods

    private Properties createdProperties() throws IOException, FileNotFoundException {
        final Properties actualProperties = new Properties();
        actualProperties.load(new FileInputStream(tmpPropertyFile));
        return actualProperties;
    }

    private Properties expectedProperties(String expectedPropertiesFileName) throws IOException,
            FileNotFoundException, URISyntaxException {
        final Properties expectedProperties = new Properties();
        final URI expectedPropertiesUri = Thread.currentThread()
                .getContextClassLoader()
                .getResource(expectedPropertiesFileName)
                .toURI();
        expectedProperties.load(new FileInputStream(new File(expectedPropertiesUri)));
        return expectedProperties;
    }

}
