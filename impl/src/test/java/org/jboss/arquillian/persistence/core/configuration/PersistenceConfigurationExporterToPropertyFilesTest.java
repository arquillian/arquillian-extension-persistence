/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.persistence.core.configuration;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.jboss.arquillian.persistence.testutils.TestConfigurationLoader;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.junit.After;
import org.junit.Test;

public class PersistenceConfigurationExporterToPropertyFilesTest
{

   private static final String ARQ_PROPERTY_FILE = "ike.properties";

   private File createdFile;

   @After
   public void deleteTemporaryFile()
   {
      if (createdFile != null && createdFile.exists())
      {
         createdFile.delete();
      }
   }

   @Test
   public void should_export_persistence_configuration_to_property_file() throws Exception
   {
      // given
      Properties expectedProperties = expectedProperties("properties/basic.arquillian.persistence.properties");
      expectedProperties.setProperty("arquillian.extension.persistence.dump.directory", System.getProperty("java.io.tmpdir"));

      PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
      persistenceConfiguration.setDefaultDataSource("DefaultDS");
      persistenceConfiguration.setDefaultTransactionMode(TransactionMode.ROLLBACK);

      // when
      Configuration.exportUsing(persistenceConfiguration)
                   .toProperties(new FileOutputStream(ARQ_PROPERTY_FILE));

      // then
      assertThat(createdProperties()).isEqualTo(expectedProperties);
   }

   @Test
   public void should_export_custom_persistence_configuration_loaded_from_xml_to_property_file() throws Exception
   {
      // given
      Properties expectedProperties = expectedProperties("properties/custom.arquillian.persistence.properties");
      PersistenceConfiguration persistenceConfiguration = TestConfigurationLoader.createPersistenceConfigurationFrom("arquillian.xml");

      // when
      Configuration.exportUsing(persistenceConfiguration)
                   .toProperties(new FileOutputStream(ARQ_PROPERTY_FILE));

      // then
      assertThat(createdProperties()).isEqualTo(expectedProperties);
   }

   // Utility methods

   private Properties createdProperties() throws IOException, FileNotFoundException
   {
      createdFile = new File(ARQ_PROPERTY_FILE);
      final Properties actualProperties = new Properties();
      actualProperties.load(new FileInputStream(createdFile));
      return actualProperties;
   }

   private Properties expectedProperties(String expectedPropertiesFileName) throws IOException,
         FileNotFoundException, URISyntaxException
   {
      final Properties expectedProperties = new Properties();
      final URI expectedPropertiesUri = Thread.currentThread()
                                              .getContextClassLoader()
                                              .getResource(expectedPropertiesFileName)
                                              .toURI();
      expectedProperties.load(new FileInputStream(new File(expectedPropertiesUri)));
      return expectedProperties;
   }

}
