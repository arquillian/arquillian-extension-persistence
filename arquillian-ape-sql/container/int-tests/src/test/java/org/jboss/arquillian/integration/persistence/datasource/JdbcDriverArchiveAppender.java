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
package org.jboss.arquillian.integration.persistence.datasource;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

import java.io.File;

public abstract class JdbcDriverArchiveAppender implements AuxiliaryArchiveAppender {

    public abstract String getDriverCoordinates();

    @Override
    public Archive<?> createAuxiliaryArchive() {
        return resolveDriverArtifact(getDriverCoordinates());
    }

    private Archive<?> resolveDriverArtifact(final String driverCoordinates) {
        PomEquippedResolveStage resolver = Maven.configureResolver().workOffline().loadPomFromFile("pom.xml");
        File[] jars = resolver.resolve(driverCoordinates).withoutTransitivity().asFile();
        return ShrinkWrap.createFromZipFile(JavaArchive.class, jars[0]);
    }

}
