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
package org.jboss.arquillian.persistence.dbunit.deployment;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.dbunit.client.DBUnitExtension;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.container.RemoteDBUnitExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Creates <code>arquillian-persistence-dbunit.jar</code> archive
 * to run Persistence Extension with DBUnit. Includes all dependencies required
 * by the extension.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DBUnitArchiveAppender implements AuxiliaryArchiveAppender
{
   @Inject
   Instance<DBUnitConfiguration> dbunitConfigurationInstance;

   @Override
   public Archive<?> createAuxiliaryArchive()
   {

      final JavaArchive dbUnitExtensionArchive = ShrinkWrap.create(JavaArchive.class, "arquillian-persistence-dbunit.jar")
                                                           .addPackages(true,
                                                                 // exclude client package
                                                                 Filters.exclude(DBUnitExtension.class.getPackage()),
                                                                 "org.jboss.arquillian.persistence.dbunit")
                                                           .addPackages(true, requiredLibraries())
                                                           .addAsServiceProvider(RemoteLoadableExtension.class, RemoteDBUnitExtension.class);

      return dbUnitExtensionArchive;
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
