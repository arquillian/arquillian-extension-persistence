/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.persistence.core.deployment;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public final class ResourceAppender {

    public static void addResources(final Archive<?> applicationArchive, final JavaArchive dataArchive) {
        if (LibraryContainer.class.isAssignableFrom(applicationArchive.getClass())) {
            final LibraryContainer<?> libraryContainer = (LibraryContainer<?>) applicationArchive;
            try {
                libraryContainer.addAsLibrary(dataArchive);
            } catch (UnsupportedOperationException e) {
                // Because of this https://github.com/shrinkwrap/shrinkwrap/blob/dcf5f87/impl-base/src/main/java/org/jboss/shrinkwrap/impl/base/spec/JavaArchiveImpl.java#L118
                applicationArchive.merge(dataArchive);
            }
        } else {
            applicationArchive.merge(dataArchive);
        }
    }
}
