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
package org.arquillian.ape.rdbms.core.lifecycle;

import org.arquillian.ape.rdbms.core.configuration.PersistenceConfiguration;
import org.arquillian.ape.rdbms.core.event.AfterPersistenceTest;
import org.arquillian.ape.rdbms.core.event.BeforePersistenceClass;
import org.arquillian.ape.rdbms.core.event.BeforePersistenceTest;
import org.arquillian.ape.rdbms.core.event.InitializeConfiguration;
import org.arquillian.ape.rdbms.core.metadata.PersistenceExtensionFeatureResolver;
import org.arquillian.ape.rdbms.core.metadata.PersistenceExtensionScriptingFeatureResolver;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.arquillian.ape.spi.datasource.DataSourceProvider;
import org.arquillian.ape.rdbms.core.datasource.JndiDataSourceProvider;
import org.arquillian.ape.rdbms.core.metadata.MetadataExtractor;
import org.arquillian.ape.rdbms.core.metadata.PersistenceExtensionEnabler;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

import javax.sql.DataSource;

/**
 * Determines if persistence extension should be triggered for the given
 * test class.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class PersistenceTestTrigger {

    @Inject
    @ClassScoped
    private InstanceProducer<MetadataExtractor> metadataExtractorProducer;

    @Inject
    @ClassScoped
    private InstanceProducer<PersistenceExtensionEnabler> persistenceExtensionEnabler;

    @Inject
    @TestScoped
    private InstanceProducer<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolverProvider;

    @Inject
    @TestScoped
    private InstanceProducer<PersistenceExtensionScriptingFeatureResolver> persistenceExtensionScriptingFeatureResolverProvider;

    @Inject
    @TestScoped
    private InstanceProducer<javax.sql.DataSource> dataSourceProducer;

    @Inject
    private Instance<PersistenceConfiguration> configurationInstance;

    @Inject
    private Instance<ScriptingConfiguration> scriptingConfigurationInstance;

    @Inject
    private Event<BeforePersistenceTest> beforePersistenceTestEvent;

    @Inject
    private Event<AfterPersistenceTest> afterPersistenceTestEvent;

    @Inject
    private Event<InitializeConfiguration> initializeConfigurationEvent;

    @Inject
    private Event<BeforePersistenceClass> beforePersistenceClassEvent;

    @Inject
    private Instance<ServiceLoader> serviceLoaderInstance;

    public void beforeClass(@Observes BeforeClass beforeClass) {
        metadataExtractorProducer.set(new MetadataExtractor(beforeClass.getTestClass()));
        persistenceExtensionEnabler.set(new PersistenceExtensionEnabler(metadataExtractorProducer.get()));

        if (persistenceExtensionEnabler.get().shouldPersistenceExtensionBeActivated()) {
            initializeConfigurationEvent.fire(new InitializeConfiguration());
            beforePersistenceClassEvent.fire(new BeforePersistenceClass(beforeClass.getTestClass()));
        }
    }

    public void beforeTest(@Observes(precedence = 25) Before beforeTestEvent) {
        PersistenceConfiguration persistenceConfiguration = configurationInstance.get();
        persistenceExtensionFeatureResolverProvider.set(new PersistenceExtensionFeatureResolver(beforeTestEvent.getTestMethod(), metadataExtractorProducer.get(), persistenceConfiguration));
        persistenceExtensionScriptingFeatureResolverProvider.set(new PersistenceExtensionScriptingFeatureResolver(beforeTestEvent.getTestMethod(), metadataExtractorProducer.get(), scriptingConfigurationInstance.get()));

        if (persistenceExtensionEnabler.get().shouldPersistenceExtensionBeActivated()) {
            createDataSource();
            beforePersistenceTestEvent.fire(new BeforePersistenceTest(beforeTestEvent));
        }

    }

    public void afterTest(@Observes(precedence = -2) After afterTestEvent) {
        if (persistenceExtensionEnabler.get().shouldPersistenceExtensionBeActivated()) {
            afterPersistenceTestEvent.fire(new AfterPersistenceTest(afterTestEvent));
        }
    }

    // Private methods

    private void createDataSource() {
        String dataSourceName = persistenceExtensionFeatureResolverProvider.get().getDataSourceName();
        dataSourceProducer.set(loadDataSource(dataSourceName));
    }

    /**
     * @param dataSourceName
     * @return
     * @throws IllegalStateException when more than one data source provider exists on the classpath
     */
    private DataSource loadDataSource(String dataSourceName) {
        final DataSourceProvider dataSourceProvider = serviceLoaderInstance.get()
                .onlyOne(DataSourceProvider.class, JndiDataSourceProvider.class);

        return dataSourceProvider.lookupDataSource(dataSourceName);
    }
}
