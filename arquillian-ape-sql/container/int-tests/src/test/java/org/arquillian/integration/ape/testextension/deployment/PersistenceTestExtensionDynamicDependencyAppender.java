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
package org.arquillian.integration.ape.testextension.deployment;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.arquillian.integration.ape.testextension.data.CleanupVerificationDataSetProvider;
import org.arquillian.ape.rdbms.core.data.descriptor.ResourceDescriptor;
import org.arquillian.ape.rdbms.core.deployment.ResourceAppender;
import org.arquillian.ape.rdbms.core.metadata.MetadataExtractor;
import org.arquillian.ape.rdbms.core.metadata.PersistenceExtensionEnabler;
import org.arquillian.ape.rdbms.dbunit.configuration.DBUnitConfiguration;
import org.arquillian.ape.rdbms.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PersistenceTestExtensionDynamicDependencyAppender implements ApplicationArchiveProcessor {

    @Inject
    Instance<DBUnitConfiguration> configuration;

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        final PersistenceExtensionEnabler persistenceExtensionEnabler = new PersistenceExtensionEnabler(testClass);
        if (!persistenceExtensionEnabler.shouldPersistenceExtensionBeActivated()) {
            return;
        }

        final CleanupVerificationDataSetProvider dataSetProvider = new CleanupVerificationDataSetProvider(testClass, new MetadataExtractor(testClass), configuration.get());
        final Collection<DataSetResourceDescriptor> dataSets = dataSetProvider.getDescriptors(testClass);
        if (!dataSets.isEmpty()) {
            ResourceAppender.addResources(applicationArchive, toJavaArchive(dataSets));
        }
    }

    // Private helper methods

    private JavaArchive toJavaArchive(final Collection<? extends ResourceDescriptor<?>> descriptors) {
        final List<String> paths = new ArrayList<String>(descriptors.size());

        for (ResourceDescriptor<?> descriptor : descriptors) {
            paths.add(descriptor.getLocation());
        }

        return createArchiveWithResources(paths.toArray(new String[descriptors.size()]));
    }

    private JavaArchive createArchiveWithResources(String... resourcePaths) {
        final JavaArchive dataSetsArchive = ShrinkWrap.create(JavaArchive.class);

        for (String path : resourcePaths) {
            dataSetsArchive.addAsResource(path);
        }

        return dataSetsArchive;
    }

}