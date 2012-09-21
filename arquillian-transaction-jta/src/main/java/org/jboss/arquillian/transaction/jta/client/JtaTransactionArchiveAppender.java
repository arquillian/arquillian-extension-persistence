/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.arquillian.transaction.jta.client;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.transaction.jta.container.JtaTransactionRemoteExtension;
import org.jboss.arquillian.transaction.jta.provider.JtaTransactionProvider;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * An archive appender that is responsible for packaging the extension.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class JtaTransactionArchiveAppender implements AuxiliaryArchiveAppender {

    /**
     * {@inheritDoc}
     */
    @Override
    public Archive<?> createAuxiliaryArchive() {

        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "arquillian-transaction-impl-jta.jar");

        archive.addPackage(JtaTransactionRemoteExtension.class.getPackage());
        archive.addPackage(JtaTransactionProvider.class.getPackage());

        archive.addAsServiceProvider(RemoteLoadableExtension.class, JtaTransactionRemoteExtension.class);

        return archive;
    }
}
