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
package org.arquillian.ape.rdbms.core.deployment;

import org.arquillian.ape.rdbms.ApplyScriptAfter;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.arquillian.ape.rdbms.CleanupUsingScript;
import org.arquillian.ape.rdbms.CreateSchema;
import org.arquillian.ape.rdbms.core.data.descriptor.ResourceDescriptor;
import org.arquillian.ape.rdbms.core.dbunit.data.descriptor.Format;
import org.arquillian.ape.rdbms.core.metadata.PersistenceExtensionEnabler;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.arquillian.ape.rdbms.script.data.provider.SqlScriptProvider;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Appends all data sets defined for the test class to the test archive.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class PersistenceExtensionDataResourcesTestArchiveEnricher implements ApplicationArchiveProcessor {

    @Inject
    Instance<ScriptingConfiguration> scriptingConfigurationInstance;

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {

        final PersistenceExtensionEnabler persistenceExtensionEnabler = new PersistenceExtensionEnabler(testClass);
        if (!persistenceExtensionEnabler.shouldPersistenceExtensionBeActivated()) {
            return;
        }

        final Set<ResourceDescriptor<?>> allDataResources = fetchAllDataResources(testClass);
        if (!allDataResources.isEmpty()) {
            addResources(applicationArchive, allDataResources);
        }
    }

    // Private helper methods

    private Set<ResourceDescriptor<?>> fetchAllDataResources(TestClass testClass) {
        final Set<ResourceDescriptor<?>> allDataSets = new HashSet<ResourceDescriptor<?>>();

        final SqlScriptProvider<ApplyScriptBefore> scriptsAppliedBeforeTestProvider = SqlScriptProvider.createProviderForScriptsToBeAppliedBeforeTest(testClass, scriptingConfigurationInstance.get());
        final SqlScriptProvider<ApplyScriptAfter> scriptsAppliedAfterTestProvider = SqlScriptProvider.createProviderForScriptsToBeAppliedAfterTest(testClass, scriptingConfigurationInstance.get());
        final SqlScriptProvider<CleanupUsingScript> cleanupScriptsProvider = SqlScriptProvider.createProviderForCleanupScripts(testClass, scriptingConfigurationInstance.get());
        final SqlScriptProvider<CreateSchema> createSchemaScripts = SqlScriptProvider.createProviderForCreateSchemaScripts(testClass, scriptingConfigurationInstance.get());

        allDataSets.addAll(scriptsAppliedBeforeTestProvider.getDescriptors(testClass));
        allDataSets.addAll(scriptsAppliedAfterTestProvider.getDescriptors(testClass));
        allDataSets.addAll(cleanupScriptsProvider.getDescriptors(testClass));
        allDataSets.addAll(createSchemaScripts.getDescriptors(testClass));

        return allDataSets;
    }

    private void addResources(Archive<?> applicationArchive, Set<ResourceDescriptor<?>> allDataResources) {
        final List<String> resources = extractPaths(allDataResources);

        if (EnterpriseArchive.class.isInstance(applicationArchive)) {
            ((EnterpriseArchive) applicationArchive).addAsLibrary(createArchiveWithResources(resources));
        } else if (ResourceContainer.class.isInstance(applicationArchive)) {
            addResourcesToApplicationArchive((ResourceContainer<?>) applicationArchive, resources);
        } else {
            throw new RuntimeException("Unsupported archive type " + applicationArchive.getClass().getName());
        }
    }

    private void addResourcesToApplicationArchive(ResourceContainer<?> applicationArchive, List<String> resourcePaths) {
        for (String path : resourcePaths) {
            applicationArchive.addAsResource(path);
        }
    }

    private JavaArchive createArchiveWithResources(Collection<String> resourcePaths) {
        final JavaArchive dataSetsArchive = ShrinkWrap.create(JavaArchive.class, "arquillian-persistence-scripts.jar");

        for (String path : resourcePaths) {
            dataSetsArchive.addAsResource(path);
        }

        return dataSetsArchive;
    }

    private List<String> extractPaths(final Collection<? extends ResourceDescriptor<?>> descriptors) {
        final List<String> paths = new ArrayList<String>(descriptors.size());

        for (ResourceDescriptor<?> descriptor : descriptors) {
            if (Format.isFileType(descriptor.getFormat())) {
                paths.add(descriptor.getLocation());
            }
        }

        return paths;
    }

}
