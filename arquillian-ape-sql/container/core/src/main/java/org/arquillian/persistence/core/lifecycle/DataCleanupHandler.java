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
package org.arquillian.persistence.core.lifecycle;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.arquillian.persistence.CleanupUsingScript;
import org.arquillian.persistence.core.event.AfterPersistenceTest;
import org.arquillian.persistence.core.event.BeforePersistenceTest;
import org.arquillian.persistence.core.event.CleanupData;
import org.arquillian.persistence.core.event.CleanupDataUsingScript;
import org.arquillian.persistence.core.metadata.PersistenceExtensionFeatureResolver;
import org.arquillian.persistence.core.metadata.PersistenceExtensionScriptingFeatureResolver;
import org.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.arquillian.persistence.script.data.provider.SqlScriptProvider;

public class DataCleanupHandler {

    @Inject
    private Instance<ScriptingConfiguration> scriptingConfigurationInstance;

    @Inject
    private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolverInstance;

    @Inject
    private Instance<PersistenceExtensionScriptingFeatureResolver> persistenceExtensionScriptingFeatureResolverInstance;

    @Inject
    private Event<CleanupData> cleanUpDataEvent;

    @Inject
    private Event<CleanupDataUsingScript> cleanUpDataUsingScriptEvent;

    public void prepareDatabase(@Observes(precedence = 40) BeforePersistenceTest beforePersistenceTest) {
        if (persistenceExtensionFeatureResolverInstance.get().shouldCleanupBefore()) {
            cleanUpDataEvent.fire(new CleanupData(beforePersistenceTest, persistenceExtensionFeatureResolverInstance.get().getCleanupStrategy()));
        }

        if (persistenceExtensionScriptingFeatureResolverInstance.get().shouldCleanupUsingScriptBefore()) {
            final SqlScriptProvider<CleanupUsingScript> scriptsProvider = SqlScriptProvider.createProviderForCleanupScripts(beforePersistenceTest.getTestClass(), scriptingConfigurationInstance.get());
            cleanUpDataUsingScriptEvent.fire(new CleanupDataUsingScript(scriptsProvider.getDescriptorsDefinedFor(beforePersistenceTest.getTestMethod())));
        }
    }

    public void verifyDatabase(@Observes(precedence = 20) AfterPersistenceTest afterPersistenceTest) {

        if (persistenceExtensionFeatureResolverInstance.get().shouldCleanupAfter()) {
            cleanUpDataEvent.fire(new CleanupData(afterPersistenceTest, persistenceExtensionFeatureResolverInstance.get().getCleanupStrategy()));
        }

        if (persistenceExtensionScriptingFeatureResolverInstance.get().shouldCleanupUsingScriptAfter()) {
            final SqlScriptProvider<CleanupUsingScript> scriptsProvider = SqlScriptProvider.createProviderForCleanupScripts(afterPersistenceTest.getTestClass(), scriptingConfigurationInstance.get());
            cleanUpDataUsingScriptEvent.fire(new CleanupDataUsingScript(scriptsProvider.getDescriptorsDefinedFor(afterPersistenceTest.getTestMethod())));
        }
    }

}
