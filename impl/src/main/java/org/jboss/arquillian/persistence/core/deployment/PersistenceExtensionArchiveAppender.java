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
package org.jboss.arquillian.persistence.core.deployment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.core.client.PersistenceExtension;
import org.jboss.arquillian.persistence.core.container.RemotePersistenceExtension;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.script.DefaultStatementSplitter;
import org.jboss.arquillian.persistence.script.oracle.OracleStatementSplitter;
import org.jboss.arquillian.persistence.spi.script.StatementSplitter;
import org.jboss.arquillian.persistence.transaction.PersistenceExtensionConventionTransactionEnabler;
import org.jboss.arquillian.transaction.spi.provider.TransactionEnabler;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 *
 * Creates <code>arquillian-persistence.jar</code> archive
 * to run Persistence Extension. Includes all dependencies required
 * by the extension.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceExtensionArchiveAppender implements AuxiliaryArchiveAppender
{
   @Inject
   Instance<ArquillianDescriptor> arquillianDescriptorInstance;

   @Inject
   Instance<DBUnitConfiguration> dbunitConfigurationInstance;

   @Override
   public Archive<?> createAuxiliaryArchive()
   {

      final JavaArchive persistenceExtensionArchive = ShrinkWrap.create(JavaArchive.class, "arquillian-persistence.jar")
                                                                .addPackages(true,
                                                                      // exclude client package
                                                                      Filters.exclude(PersistenceExtension.class.getPackage()),
                                                                      "org.jboss.arquillian.persistence")
                                                                .addPackages(true, requiredLibraries())
                                                                .addAsServiceProvider(RemoteLoadableExtension.class, RemotePersistenceExtension.class)
                                                                .addAsServiceProviderAndClasses(StatementSplitter.class, DefaultStatementSplitter.class, OracleStatementSplitter.class)
                                                                .addAsServiceProvider(TransactionEnabler.class, PersistenceExtensionConventionTransactionEnabler.class);

      return persistenceExtensionArchive;
   }

   // Private helper methods

   private String[] requiredLibraries()
   {
      List<String> libraries = new ArrayList<String>(Arrays.asList(
            "org.dbunit",
            "org.apache.commons",
            "org.apache.log4j",
            "org.slf4j",
            "org.yaml",
            "org.codehaus.jackson"
      ));

      if (!dbunitConfigurationInstance.get().isExcludePoi())
      {
         libraries.add("org.apache.poi");
      }

      return libraries.toArray(new String[libraries.size()]);
   }


}
