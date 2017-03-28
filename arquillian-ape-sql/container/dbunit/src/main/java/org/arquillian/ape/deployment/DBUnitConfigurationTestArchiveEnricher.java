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
package org.arquillian.ape.deployment;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.arquillian.persistence.core.configuration.PropertiesSerializer;
import org.arquillian.persistence.core.deployment.ResourceAppender;
import org.arquillian.persistence.core.metadata.PersistenceExtensionEnabler;
import org.arquillian.ape.configuration.DBUnitConfiguration;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Appends all data sets defined for the test class to the test archive.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DBUnitConfigurationTestArchiveEnricher implements ApplicationArchiveProcessor {

    private static final Logger log = Logger.getLogger(DBUnitConfigurationTestArchiveEnricher.class.getName());

    @Inject
    Instance<ArquillianDescriptor> arquillianDescriptorInstance;

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {

        final PersistenceExtensionEnabler persistenceExtensionEnabler = new PersistenceExtensionEnabler(testClass);
        if (!persistenceExtensionEnabler.shouldPersistenceExtensionBeActivated()) {
            return;
        }
        final JavaArchive additionalPersistenceResources = ShrinkWrap.create(JavaArchive.class, "arquillian-persistence-dbunit-additional-resources.jar");
        merge(additionalPersistenceResources, dbUnitConfigurationSerializedAsProperties());
        ResourceAppender.addResources(applicationArchive, additionalPersistenceResources);
    }

    // Private helper methods

    private void merge(final JavaArchive target, final JavaArchive... archivesToMerge) {
        for (JavaArchive archiveToMerge : archivesToMerge) {
            target.merge(archiveToMerge);
        }
    }

    private JavaArchive dbUnitConfigurationSerializedAsProperties() {
        final DBUnitConfiguration dbUnitConfigurationPrototype = new DBUnitConfiguration();
        final Map<String, String> extensionProperties = extractExtensionProperties(arquillianDescriptorInstance.get(), dbUnitConfigurationPrototype.getQualifier());
        final ByteArrayOutputStream properties = new PropertiesSerializer(dbUnitConfigurationPrototype.getPrefix()).serializeToProperties(extensionProperties);
        return ShrinkWrap.create(JavaArchive.class).addAsResource(new ByteArrayAsset(properties.toByteArray()), new DBUnitConfiguration().getPrefix() + "properties");
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

}
