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
package org.arquillian.ape.rdbms.core.configuration;

import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class PersistenceDescriptorExtractor {

    private static final String WAR_AND_JAR = ".*\\.war|.*\\.jar";

    /**
     * Returns open stream of persistence.xml found in the archive, but
     * only if single file have been found.
     *
     * @param archive
     * @return Input stream of persistence.xml found or null if zero or multiple found in the archive.
     */
    public InputStream getAsStream(final Archive<?> archive) {
        final Archive<?> testable = findTestableArchive(archive);
        final Collection<Node> values = collectPersistenceXml(testable);
        if (values.size() == 1) {
            return values.iterator().next().getAsset().openStream();
        }

        return null;
    }

    /**
     * Inspects archive in order to find nested testable archive, assuming
     *
     * @param archive
     * @return testable archive or passed one if nothing found
     */
    private Archive<?> findTestableArchive(final Archive<?> archive) {
        final Map<ArchivePath, Node> nestedArchives = archive.getContent(Filters.include(WAR_AND_JAR));
        if (!nestedArchives.isEmpty()) {
            for (ArchivePath path : nestedArchives.keySet()) {
                try {
                    GenericArchive genericArchive = archive.getAsType(GenericArchive.class, path);
                    if (genericArchive != null && Testable.isArchiveToTest(genericArchive)) {
                        return genericArchive;
                    }
                } catch (IllegalArgumentException e) {
                    // no-op, Nested archive is not a ShrinkWrap archive.
                }
            }
        }

        return archive;
    }

    /**
     * Recursively scans archive content (including sub archives) for persistence.xml descriptors.
     *
     * @param archive
     * @return
     */
    private Collection<Node> collectPersistenceXml(final Archive<?> archive) {
        final Collection<Node> nodes = new LinkedList<Node>(getPersistenceDescriptors(archive));
        for (Node node : collectSubArchives(archive)) {
            if (node.getAsset() instanceof ArchiveAsset) {
                final ArchiveAsset archiveAsset = (ArchiveAsset) node.getAsset();
                nodes.addAll(collectPersistenceXml(archiveAsset.getArchive()));
            }
        }
        return nodes;
    }

    private Collection<Node> getPersistenceDescriptors(final Archive<?> archive) {
        return archive.getContent(Filters.include(".*persistence.xml")).values();
    }

    private Collection<Node> collectSubArchives(final Archive<?> archive) {
        return archive.getContent(Filters.include(WAR_AND_JAR)).values();
    }
}
