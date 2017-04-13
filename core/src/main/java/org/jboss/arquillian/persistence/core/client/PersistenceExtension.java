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
package org.jboss.arquillian.persistence.core.client;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.persistence.core.deployment.PersistenceExtensionArchiveAppender;
import org.jboss.arquillian.persistence.core.deployment.PersistenceExtensionConfigurationTestArchiveEnricher;
import org.jboss.arquillian.persistence.core.deployment.PersistenceExtensionDataResourcesTestArchiveEnricher;
import org.jboss.arquillian.persistence.core.enricher.PersistenceConfigurationEnricher;
import org.jboss.arquillian.persistence.core.enricher.ScriptingConfigurationEnricher;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfigurationClientSideProducer;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * Wires persistence extension services which are used to prepare
 * packages used in container.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class PersistenceExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, PersistenceExtensionArchiveAppender.class)
            .service(ApplicationArchiveProcessor.class, PersistenceExtensionConfigurationTestArchiveEnricher.class)
            .service(ApplicationArchiveProcessor.class, PersistenceExtensionDataResourcesTestArchiveEnricher.class)
            .service(ResourceProvider.class, PersistenceConfigurationEnricher.class)
            .service(ResourceProvider.class, ScriptingConfigurationEnricher.class)
            .observer(PersistenceConfigurationClientSideProducer.class)
            .observer(ScriptingConfigurationClientSideProducer.class)
            .observer(SchemaCreationCoordinator.class)
            .observer(DatabaseStateDumper.class);
    }
}
