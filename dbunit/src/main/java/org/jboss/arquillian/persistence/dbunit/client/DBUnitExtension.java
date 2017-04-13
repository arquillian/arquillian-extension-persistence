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
package org.jboss.arquillian.persistence.dbunit.client;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfigurationClientSideProducer;
import org.jboss.arquillian.persistence.dbunit.deployment.DBUnitArchiveAppender;
import org.jboss.arquillian.persistence.dbunit.deployment.DBUnitConfigurationTestArchiveEnricher;
import org.jboss.arquillian.persistence.dbunit.deployment.DBUnitDataSetsTestArchiveEnricher;
import org.jboss.arquillian.persistence.dbunit.enricher.DBUnitConfigurationEnricher;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * Wires persistence extension services which are used to prepare
 * packages used in container.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DBUnitExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, DBUnitArchiveAppender.class)
            .service(ApplicationArchiveProcessor.class, DBUnitConfigurationTestArchiveEnricher.class)
            .service(ApplicationArchiveProcessor.class, DBUnitDataSetsTestArchiveEnricher.class)
            .service(ResourceProvider.class, DBUnitConfigurationEnricher.class)
            .observer(DBUnitConfigurationClientSideProducer.class);
    }
}
