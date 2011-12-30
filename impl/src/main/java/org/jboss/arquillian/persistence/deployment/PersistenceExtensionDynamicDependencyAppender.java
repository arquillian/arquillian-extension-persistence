/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.persistence.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.descriptor.ResourceDescriptor;
import org.jboss.arquillian.persistence.data.script.ScriptHelper;
import org.jboss.arquillian.persistence.metadata.DataSetProvider;
import org.jboss.arquillian.persistence.metadata.ExpectedDataSetProvider;
import org.jboss.arquillian.persistence.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.metadata.SqlScriptProvider;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * Appends all data sets defined for the test class to the test archive.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceExtensionDynamicDependencyAppender implements ApplicationArchiveProcessor
{

   @Inject
   Instance<PersistenceConfiguration> configuration;

   @Override
   public void process(Archive<?> applicationArchive, TestClass testClass)
   {
      addResources(applicationArchive, toJavaArchive(fetchAllDataResources(testClass)));
      addSqlScriptAsResource(applicationArchive, configuration.get().getCleanupStatement());
      addSqlScriptAsResource(applicationArchive, configuration.get().getInitStatement());
   }

   // Private helper methods

   private Set<ResourceDescriptor<?>> fetchAllDataResources(TestClass testClass)
   {
      final Set<ResourceDescriptor<?>> allDataSets = new HashSet<ResourceDescriptor<?>>();

      final DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testClass), configuration.get());
      final ExpectedDataSetProvider expectedDataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testClass), configuration.get());
      final SqlScriptProvider sqlScriptsProvider = new SqlScriptProvider(new MetadataExtractor(testClass), configuration.get());

      allDataSets.addAll(dataSetProvider.getDescriptors(testClass));
      allDataSets.addAll(expectedDataSetProvider.getDescriptors(testClass));
      allDataSets.addAll(sqlScriptsProvider.getDescriptors(testClass));

      return allDataSets;
   }

   private void addResources(Archive<?> applicationArchive, final JavaArchive dataArchive)
   {
      if (applicationArchive instanceof JavaArchive)
      {
         addAsResource(applicationArchive, dataArchive);
      }
      else
      {
         addAsLibrary(applicationArchive, dataArchive);
      }
   }

   private void addAsResource(Archive<?> applicationArchive, JavaArchive dataArchive)
   {
      applicationArchive.merge(dataArchive);
   }

   private void addAsLibrary(Archive<?> applicationArchive, JavaArchive dataArchive)
   {
      final LibraryContainer<?> libraryContainer = (LibraryContainer<?>) applicationArchive;
      libraryContainer.addAsLibrary(dataArchive);
   }

   private JavaArchive toJavaArchive(final Collection<? extends ResourceDescriptor<?>> descriptors)
   {
      final List<String> paths = new ArrayList<String>(descriptors.size());

      for (ResourceDescriptor<?> descriptor : descriptors)
      {
         paths.add(descriptor.getLocation());
      }

      return createArchiveWithResources(paths.toArray(new String[descriptors.size()]));
   }

   private JavaArchive createArchiveWithResources(String ... resourcePaths)
   {
      final JavaArchive dataSetsArchive = ShrinkWrap.create(JavaArchive.class);

      for (String path : resourcePaths)
      {
         dataSetsArchive.addAsResource(path);
      }

      return dataSetsArchive;
   }

   private void addSqlScriptAsResource(Archive<?> applicationArchive, String script)
   {
      if (ScriptHelper.isSqlScriptFile(script))
      {
         addResources(applicationArchive, createArchiveWithResources(script));
      }
   }

}
