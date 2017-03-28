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
package org.arquillian.persistence.script.configuration;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.arquillian.persistence.core.configuration.Configuration;
import org.arquillian.persistence.testutils.TestConfigurationLoader;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class ScriptingConfigurationImporterFromXmlTest {

    private ScriptingConfiguration configuration;

    @Before
    public void initialize() {
        configuration = new ScriptingConfiguration();
    }

    @Test
    public void should_extract_init_statement_from_external_configuration_file() throws Exception {
        // given
        String expectedInitStatement = "SELECT * FROM ARQUILLIAN_TESTS";
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian-script.xml");

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.getScriptsToExecuteBeforeTest()).containsOnly(expectedInitStatement);
    }

    @Test
    public void should_extract_script_charset_from_external_configuration_file() throws Exception {
        // given
        Charset expectedCharset = Charset.forName("ISO-8859-1");
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian-script.xml");

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.getCharset()).isEqualTo(expectedCharset);
    }

    @Test
    public void should_use_default_charset_if_not_specified_in_the_configuration() throws Exception {
        // given
        Charset utf8 = Charset.forName("UTF-8");
        ArquillianDescriptor descriptor = TestConfigurationLoader.createArquillianDescriptor("arquillian.xml");

        // when
        Configuration.importTo(configuration).from(descriptor);

        // then
        assertThat(configuration.getCharset()).isEqualTo(utf8);
    }

}
