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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.core.client.PersistenceExtension;
import org.jboss.arquillian.persistence.core.configuration.Configuration;
import org.jboss.arquillian.persistence.core.configuration.ConfigurationExporter;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.core.configuration.PropertiesSerializer;
import org.jboss.arquillian.persistence.core.container.RemotePersistenceExtension;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.jpacacheeviction.JpaCacheEvictionConfiguration;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
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
   Instance<PersistenceConfiguration> persistenceConfigurationInstance;

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
                                                                .addAsServiceProvider(RemoteLoadableExtension.class, RemotePersistenceExtension.class);

      addPersistenceConfigurationSerializedAsProperties(persistenceExtensionArchive);
      addDBUnitConfigurationSerializedAsProperties(persistenceExtensionArchive);
      addJpaCacheEvictionConfigurationSerizedAsProperties(persistenceExtensionArchive);
      
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


   private void addPersistenceConfigurationSerializedAsProperties(final JavaArchive archiveToExtend)
   {
      archiveToExtend.addAsResource(new ByteArrayAsset(exportPersistenceConfigurationAsProperties().toByteArray()), persistenceConfigurationInstance.get().getPrefix() + "properties");
   }

   private ByteArrayOutputStream exportPersistenceConfigurationAsProperties()
   {
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      final ConfigurationExporter<PersistenceConfiguration> exporter = new ConfigurationExporter<PersistenceConfiguration>(persistenceConfigurationInstance.get());
      exporter.toProperties(output);
      return output;
   }

   private void addDBUnitConfigurationSerializedAsProperties(final JavaArchive archiveToExtend)
   {
      final DBUnitConfiguration dbUnitConfigurationPrototype = new DBUnitConfiguration();
      final Map<String, String> extensionProperties = extractExtensionProperties(arquillianDescriptorInstance.get(), dbUnitConfigurationPrototype.getQualifier());
      final ByteArrayOutputStream properties = new PropertiesSerializer(dbUnitConfigurationPrototype.getPrefix()).serializeToProperties(extensionProperties);
      archiveToExtend.addAsResource(new ByteArrayAsset(properties.toByteArray()), new DBUnitConfiguration().getPrefix() + "properties");
   }

   // TODO extract to dedicated class
   private Map<String, String> extractExtensionProperties(ArquillianDescriptor descriptor, String qualifier)
   {
      final Map<String, String> extensionProperties = new HashMap<String, String>();
      for (ExtensionDef extension : descriptor.getExtensions())
      {
         if (extension.getExtensionName().equals(qualifier))
         {
            extensionProperties.putAll(extension.getExtensionProperties());
            break;
         }
      }
      return extensionProperties;
   }

   private void addJpaCacheEvictionConfigurationSerizedAsProperties(JavaArchive archiveToExtend)
   {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      JpaCacheEvictionConfiguration config = new JpaCacheEvictionConfiguration();

      Configuration.importTo(config).loadFrom(arquillianDescriptorInstance.get());
      Configuration.exportUsing(config).toProperties(output);

      archiveToExtend.addAsResource(new ByteArrayAsset(output.toByteArray()), config.getPrefix() + "properties");
   }

}
