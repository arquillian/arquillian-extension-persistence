/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.persistence.core.deployment;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.core.configuration.*;
import org.jboss.arquillian.persistence.core.exception.MultiplePersistenceUnitsException;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionEnabler;
import org.jboss.arquillian.persistence.jpa.cache.JpaCacheEvictionConfiguration;
import org.jboss.arquillian.persistence.script.ScriptLoader;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Appends all data sets defined for the test class to the test archive.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class PersistenceExtensionConfigurationTestArchiveEnricher implements ApplicationArchiveProcessor {

    private static final Logger log = Logger.getLogger(PersistenceExtensionConfigurationTestArchiveEnricher.class.getName());

    @Inject
    Instance<PersistenceConfiguration> configurationInstance;

    @Inject
    Instance<ScriptingConfiguration> scriptingConfigurationInstance;

    @Inject
    Instance<ArquillianDescriptor> arquillianDescriptorInstance;

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {

        final PersistenceExtensionEnabler persistenceExtensionEnabler = new PersistenceExtensionEnabler(testClass);
        if (!persistenceExtensionEnabler.shouldPersistenceExtensionBeActivated()) {
            return;
        }
        obtainDataSourceFromPersistenceXml(applicationArchive);
        final JavaArchive additionalPersistenceResources = ShrinkWrap.create(JavaArchive.class, "arquillian-persistence-core-additional-resources.jar");
        merge(additionalPersistenceResources, sqlScriptsAsResource(scriptingConfigurationInstance.get().getScriptsToExecuteAfterTest()),
                sqlScriptsAsResource(scriptingConfigurationInstance.get().getScriptsToExecuteBeforeTest()),
                persistenceConfigurationSerializedAsProperties(),
                scriptingConfigurationSerializedAsProperties(),
                jpaCacheEvictionConfigurationSerializedAsProperties());
        addResources(applicationArchive, additionalPersistenceResources);
    }

    // Private helper methods

    private void obtainDataSourceFromPersistenceXml(final Archive<?> applicationArchive) {
        if (configurationInstance.get().isDefaultDataSourceDefined()) {
            return; // if defined globally the only way to alter it is on test level using @DataSource annotation
        }

        final PersistenceDescriptorExtractor persistenceDescriptorArchiveExtractor = new PersistenceDescriptorExtractor();
        final InputStream persistenceXmlAsStream = persistenceDescriptorArchiveExtractor.getAsStream(applicationArchive);
        if (persistenceXmlAsStream != null) {
            final PersistenceDescriptorParser parser = new PersistenceDescriptorParser();
            try {
                final String dataSourceName = parser.obtainDataSourceName(persistenceXmlAsStream);
                configurationInstance.get().setDefaultDataSource(dataSourceName);

            } catch (MultiplePersistenceUnitsException e) {
                log.info("Unable to deduct data source from test's archive persistence.xml. " + e.getMessage());
            }
        }

    }

    private void merge(final JavaArchive target, final JavaArchive... archivesToMerge) {
        for (JavaArchive archiveToMerge : archivesToMerge) {
            target.merge(archiveToMerge);
        }
    }

    private JavaArchive persistenceConfigurationSerializedAsProperties() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsResource(new ByteArrayAsset(exportPersistenceConfigurationAsProperties().toByteArray()), configurationInstance.get().getPrefix() + "properties");
    }

    private ByteArrayOutputStream exportPersistenceConfigurationAsProperties() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ConfigurationExporter<PersistenceConfiguration> exporter = new ConfigurationExporter<PersistenceConfiguration>(configurationInstance.get());
        exporter.toProperties(output);
        return output;
    }

    private JavaArchive scriptingConfigurationSerializedAsProperties() {
        final ScriptingConfiguration scriptingConfigurationPrototype = new ScriptingConfiguration();
        final Map<String, String> extensionProperties = extractExtensionProperties(arquillianDescriptorInstance.get(), scriptingConfigurationPrototype.getQualifier());
        final ByteArrayOutputStream properties = new PropertiesSerializer(scriptingConfigurationPrototype.getPrefix()).serializeToProperties(extensionProperties);
        return ShrinkWrap.create(JavaArchive.class)
                .addAsResource(new ByteArrayAsset(properties.toByteArray()), new ScriptingConfiguration().getPrefix() + "properties");
    }

    private JavaArchive jpaCacheEvictionConfigurationSerializedAsProperties() {
        final JpaCacheEvictionConfiguration config = new JpaCacheEvictionConfiguration();
        final Map<String, String> extensionProperties = extractExtensionProperties(arquillianDescriptorInstance.get(), config.getQualifier());
        final ByteArrayOutputStream output = new PropertiesSerializer(config.getPrefix()).serializeToProperties(extensionProperties);
        return ShrinkWrap.create(JavaArchive.class)
                .addAsResource(new ByteArrayAsset(output.toByteArray()), new JpaCacheEvictionConfiguration().getPrefix() + "properties");
    }

    private JavaArchive sqlScriptsAsResource(final String... scripts) {
        if (scripts == null) {
            return ShrinkWrap.create(JavaArchive.class);
        }

        final JavaArchive sqlScriptsArchive = ShrinkWrap.create(JavaArchive.class);

        for (String script : scripts) {
            if (ScriptLoader.isSqlScriptFile(script)) {
                sqlScriptsArchive.merge(createArchiveWithResources(script));
            }
        }

        return sqlScriptsArchive;
    }

    private Map<String, String> extractExtensionProperties(ArquillianDescriptor descriptor, String qualifier) {
        final Map<String, String> extensionProperties = new HashMap<String, String>();
        for (ExtensionDef extension : descriptor.getExtensions()) {
            if (extension.getExtensionName().equals(qualifier)) {
                extensionProperties.putAll(extension.getExtensionProperties());
                break;
            }
        }
        return extensionProperties;
    }

    private void addResources(Archive<?> applicationArchive, final JavaArchive dataArchive) {
        if (JavaArchive.class.isInstance(applicationArchive)) {
            applicationArchive.merge(dataArchive);
        } else {
            final LibraryContainer<?> libraryContainer = (LibraryContainer<?>) applicationArchive;
            libraryContainer.addAsLibrary(dataArchive);
        }
    }

    private JavaArchive createArchiveWithResources(String... resourcePaths) {
        final JavaArchive dataSetsArchive = ShrinkWrap.create(JavaArchive.class);

        for (String path : resourcePaths) {
            dataSetsArchive.addAsResource(path);
        }

        return dataSetsArchive;
    }

}
