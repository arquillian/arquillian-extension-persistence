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
package org.jboss.arquillian.persistence.core.container;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.persistence.core.lifecycle.CustomScriptsExecutor;
import org.jboss.arquillian.persistence.core.lifecycle.DataCleanupHandler;
import org.jboss.arquillian.persistence.core.lifecycle.DataScriptsHandler;
import org.jboss.arquillian.persistence.core.lifecycle.ErrorCollectorHandler;
import org.jboss.arquillian.persistence.core.lifecycle.PersistenceTestTrigger;
import org.jboss.arquillian.persistence.core.lifecycle.SchemaCreationScriptsExecutor;
import org.jboss.arquillian.persistence.jpa.cache.JpaCacheEvictionHandler;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfigurationRemoteProducer;
import org.jboss.arquillian.persistence.transaction.PersistenceExtensionConventionTransactionEnabler;
import org.jboss.arquillian.transaction.spi.provider.TransactionEnabler;

/**
 * Defines all the bindings for Arquillian extension run in the
 * container.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class RemotePersistenceExtension implements RemoteLoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {

        registerTestLifecycleHandlers(builder);
        registerThirdPartyServices(builder);

        builder.observer(PersistenceConfigurationRemoteProducer.class)
            .observer(ScriptingConfigurationRemoteProducer.class)
            .observer(CommandServiceProducer.class)
            .observer(JpaCacheEvictionHandler.class);
    }

    private void registerTestLifecycleHandlers(ExtensionBuilder builder) {
        builder.observer(PersistenceTestTrigger.class)
            .observer(ErrorCollectorHandler.class)         // Order of execution Before / after test
            .observer(SchemaCreationScriptsExecutor.class) // Wraps around BeforePersistenceTest
            .observer(CustomScriptsExecutor.class)         // 50 / 10
            .observer(DataCleanupHandler.class)            // 40 / 20
            .observer(DataScriptsHandler.class);           // 30 / 40
    }

    private void registerThirdPartyServices(ExtensionBuilder builder) {
        builder.service(TransactionEnabler.class, PersistenceExtensionConventionTransactionEnabler.class);
    }
}
