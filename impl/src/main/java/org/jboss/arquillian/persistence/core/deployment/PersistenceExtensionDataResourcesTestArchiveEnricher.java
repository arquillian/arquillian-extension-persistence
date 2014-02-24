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
package org.jboss.arquillian.persistence.core.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.core.data.descriptor.DtdFileResourceDescriptor;
import org.jboss.arquillian.persistence.core.data.descriptor.Format;
import org.jboss.arquillian.persistence.core.data.descriptor.ResourceDescriptor;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionEnabler;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.jboss.arquillian.persistence.dbunit.data.provider.DataSetProvider;
import org.jboss.arquillian.persistence.dbunit.data.provider.ExpectedDataSetProvider;
import org.jboss.arquillian.persistence.dbunit.dataset.xml.DtdResolver;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.persistence.script.data.provider.SqlScriptProvider;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import static org.jboss.arquillian.persistence.core.data.descriptor.Format.isFileType;

/**
 * Appends all data sets defined for the test class to the test archive.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceExtensionDataResourcesTestArchiveEnricher implements ApplicationArchiveProcessor
{

   @Inject
   Instance<DBUnitConfiguration> dbunitConfigurationInstance;

   @Inject
   Instance<ScriptingConfiguration> scriptingConfigurationInstance;

   @Override
   public void process(Archive<?> applicationArchive, TestClass testClass)
   {

      final PersistenceExtensionEnabler persistenceExtensionEnabler = new PersistenceExtensionEnabler(testClass);
      if (!persistenceExtensionEnabler.shouldPersistenceExtensionBeActivated())
      {
         return;
      }

      final Set<ResourceDescriptor<?>> allDataResources = fetchAllDataResources(testClass);
      if (!allDataResources.isEmpty())
      {
         addResources(applicationArchive, toJavaArchive(allDataResources));
      }
   }

   // Private helper methods

   private void addResources(Archive<?> applicationArchive, final JavaArchive dataArchive)
   {
      if (JavaArchive.class.isInstance(applicationArchive))
      {
         applicationArchive.merge(dataArchive);
      }
      else
      {
         final LibraryContainer<?> libraryContainer = (LibraryContainer<?>) applicationArchive;
         libraryContainer.addAsLibrary(dataArchive);
      }
   }

   private JavaArchive toJavaArchive(final Collection<? extends ResourceDescriptor<?>> descriptors)
   {
      final List<String> paths = new ArrayList<String>(descriptors.size());

      for (ResourceDescriptor<?> descriptor : descriptors)
      {
         if (isFileType(descriptor.getFormat()))
         {
            paths.add(descriptor.getLocation());
         }
      }

      return createArchiveWithResources(paths.toArray(new String[paths.size()]));
   }

   private Set<ResourceDescriptor<?>> fetchAllDataResources(TestClass testClass)
   {
      final Set<ResourceDescriptor<?>> allDataSets = new HashSet<ResourceDescriptor<?>>();

      final DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testClass), dbunitConfigurationInstance.get());
      final ExpectedDataSetProvider expectedDataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testClass), dbunitConfigurationInstance.get());
      final SqlScriptProvider<ApplyScriptBefore> scriptsAppliedBeforeTestProvider = SqlScriptProvider.createProviderForScriptsToBeAppliedBeforeTest(testClass, scriptingConfigurationInstance.get());
      final SqlScriptProvider<ApplyScriptAfter> scriptsAppliedAfterTestProvider = SqlScriptProvider.createProviderForScriptsToBeAppliedAfterTest(testClass, scriptingConfigurationInstance.get());
      final SqlScriptProvider<CleanupUsingScript> cleanupScriptsProvider = SqlScriptProvider.createProviderForCleanupScripts(testClass, scriptingConfigurationInstance.get());
      final SqlScriptProvider<CreateSchema> createSchemaScripts = SqlScriptProvider.createProviderForCreateSchemaScripts(testClass, scriptingConfigurationInstance.get());

      allDataSets.addAll(dataSetProvider.getDescriptors(testClass));
      allDataSets.addAll(expectedDataSetProvider.getDescriptors(testClass));
      allDataSets.addAll(extractDtds(dataSetProvider.getDescriptors(testClass)));
      allDataSets.addAll(extractDtds(expectedDataSetProvider.getDescriptors(testClass)));
      allDataSets.addAll(scriptsAppliedBeforeTestProvider.getDescriptors(testClass));
      allDataSets.addAll(scriptsAppliedAfterTestProvider.getDescriptors(testClass));
      allDataSets.addAll(cleanupScriptsProvider.getDescriptors(testClass));
      allDataSets.addAll(createSchemaScripts.getDescriptors(testClass));

      return allDataSets;
   }

   private Collection<DtdFileResourceDescriptor> extractDtds(Collection<DataSetResourceDescriptor> descriptors)
   {
      final Collection<DtdFileResourceDescriptor> dtds = new ArrayList<DtdFileResourceDescriptor>();
      final DtdResolver dtdResolver = new DtdResolver();
      for (DataSetResourceDescriptor dataSet : descriptors)
      {
         if (Format.XML.equals(dataSet.getFormat()))
         {
            final String dtd = dtdResolver.resolveDtdLocationFullPath(dataSet.getLocation());
            if (dtd != null)
            {
               dtds.add(new DtdFileResourceDescriptor(dtd));
            }

         }
      }
      return dtds;
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

}
