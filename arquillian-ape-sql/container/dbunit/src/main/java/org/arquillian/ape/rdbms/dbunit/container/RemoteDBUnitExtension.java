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
package org.arquillian.ape.rdbms.dbunit.container;

import org.arquillian.ape.rdbms.dbunit.DBUnitDataHandler;
import org.arquillian.ape.rdbms.dbunit.DBUnitDataStateLogger;
import org.arquillian.ape.rdbms.dbunit.DBUnitDatabaseConnectionProvider;
import org.arquillian.ape.rdbms.dbunit.DBUnitPersistenceTestLifecycleHandler;
import org.arquillian.ape.rdbms.dbunit.configuration.DBUnitConfigurationRemoteProducer;
import org.arquillian.ape.rdbms.dbunit.lifecycle.DataSetHandler;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * Defines all the bindings for Arquillian DBUnit extension run in the
 * container.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class RemoteDBUnitExtension implements RemoteLoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        registerDBUnitTestLifecycleHandlers(builder);
        registerDBUnitHandlers(builder);
    }

    private void registerDBUnitHandlers(ExtensionBuilder builder) {
        builder.observer(DBUnitDataHandler.class)
            .observer(DBUnitConfigurationRemoteProducer.class)
            .observer(DBUnitPersistenceTestLifecycleHandler.class)
            .observer(DBUnitDataStateLogger.class)
            .service(ResourceProvider.class, DBUnitDatabaseConnectionProvider.class);
    }

    private void registerDBUnitTestLifecycleHandlers(ExtensionBuilder builder) {
        builder.observer(DataSetHandler.class);               // 20 / 30
    }
}
