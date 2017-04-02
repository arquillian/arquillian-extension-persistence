/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.persistence.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.persistence.core.configuration.Configuration;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

public class TestConfigurationLoader {

    private static final String DEFAULT_CONFIG_FILENAME = "arquillian.xml";

    private static final String DBUNIT_CONFIG_FILENAME = "arquillian-dbunit.xml";

    private static final String SCRIPT_CONFIG_FILENAME = "arquillian-script.xml";

    public static InputStream loadArquillianConfiguration(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    public static ArquillianDescriptor createArquillianDescriptor(String fileName) {
        return Descriptors.importAs(ArquillianDescriptor.class).fromStream(
            TestConfigurationLoader.loadArquillianConfiguration(fileName));
    }

    public static ArquillianDescriptor createArquillianDescriptorFromDefaultConfigurationFile() {
        return createArquillianDescriptor(DEFAULT_CONFIG_FILENAME);
    }

    public static PersistenceConfiguration createDefaultConfiguration() {
        return createPersistenceConfigurationFrom(DEFAULT_CONFIG_FILENAME);
    }

    public static DBUnitConfiguration createDefaultDBUnitConfiguration() {
        return createDBUnitConfigurationFrom(DBUNIT_CONFIG_FILENAME);
    }

    public static ScriptingConfiguration createDefaultScriptingConfiguration() {
        return createScriptingConfigurationFrom(SCRIPT_CONFIG_FILENAME);
    }

    public static Properties createPropertiesFromCustomConfigurationFile() throws IOException {
        return createPropertiesFrom("properties/custom.arquillian.persistence.properties");
    }

    public static Properties createPropertiesFrom(String fileName) throws IOException {
        final Properties properties = new Properties();
        properties.load(loadArquillianConfiguration(fileName));
        return properties;
    }

    public static PersistenceConfiguration createPersistenceConfigurationFrom(String fileName) {
        ArquillianDescriptor descriptor = createArquillianDescriptor(fileName);
        PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
        Configuration.importTo(persistenceConfiguration).from(descriptor);
        return persistenceConfiguration;
    }

    public static DBUnitConfiguration createDBUnitConfigurationFrom(String fileName) {
        ArquillianDescriptor descriptor = createArquillianDescriptor(fileName);
        DBUnitConfiguration dbunitConfiguration = new DBUnitConfiguration();
        Configuration.importTo(dbunitConfiguration).from(descriptor);
        return dbunitConfiguration;
    }

    public static ScriptingConfiguration createScriptingConfigurationFrom(String fileName) {
        ArquillianDescriptor descriptor = createArquillianDescriptor(fileName);
        ScriptingConfiguration scriptingConfiguration = new ScriptingConfiguration();
        Configuration.importTo(scriptingConfiguration).from(descriptor);
        return scriptingConfiguration;
    }
}
