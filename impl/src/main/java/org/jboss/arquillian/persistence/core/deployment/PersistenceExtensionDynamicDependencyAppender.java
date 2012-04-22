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
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.core.data.descriptor.ResourceDescriptor;
import org.jboss.arquillian.persistence.core.data.naming.PrefixedScriptFileNamingStrategy;
import org.jboss.arquillian.persistence.core.data.provider.SqlScriptProvider;
import org.jboss.arquillian.persistence.core.data.script.ScriptHelper;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionEnabler;
import org.jboss.arquillian.persistence.core.metadata.ValueExtractor;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.data.provider.DataSetProvider;
import org.jboss.arquillian.persistence.dbunit.data.provider.ExpectedDataSetProvider;
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
   Instance<PersistenceConfiguration> configurationInstance;

   @Inject
   Instance<DBUnitConfiguration> dbunitConfigurationInstance;

   @Override
   public void process(Archive<?> applicationArchive, TestClass testClass)
   {

      final PersistenceExtensionEnabler persistenceExtensionEnabler = new PersistenceExtensionEnabler(testClass);
      if (!persistenceExtensionEnabler.isPersistenceExtensionRequired())
      {
         return;
      }

      final Set<ResourceDescriptor<?>> allDataResources = fetchAllDataResources(testClass);
      if (!allDataResources.isEmpty())
      {
         addResources(applicationArchive, toJavaArchive(allDataResources));
      }

      addSqlScriptsAsResource(applicationArchive, configurationInstance.get().getScriptsToExecuteAfterTest());
      addSqlScriptsAsResource(applicationArchive, configurationInstance.get().getScriptsToExecuteBeforeTest());
   }

   // Private helper methods

   private Set<ResourceDescriptor<?>> fetchAllDataResources(TestClass testClass)
   {
      final Set<ResourceDescriptor<?>> allDataSets = new HashSet<ResourceDescriptor<?>>();

      final DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testClass), dbunitConfigurationInstance.get());
      final ExpectedDataSetProvider expectedDataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testClass), dbunitConfigurationInstance.get());
      final SqlScriptProvider<ApplyScriptBefore> scriptsAppliedBeforeTestProvider = createProviderForScriptsToBeAppliedBeforeTest(testClass);
      final SqlScriptProvider<ApplyScriptAfter> scriptsAppliedAfterTestProvider = createProviderForScriptsToBeAppliedAfterTest(testClass);
      final SqlScriptProvider<CleanupUsingScript> cleanupScriptsProvider = createProviderForCleanupScripts(testClass);

      allDataSets.addAll(dataSetProvider.getDescriptors(testClass));
      allDataSets.addAll(expectedDataSetProvider.getDescriptors(testClass));
      allDataSets.addAll(scriptsAppliedBeforeTestProvider.getDescriptors(testClass));
      allDataSets.addAll(scriptsAppliedAfterTestProvider.getDescriptors(testClass));
      allDataSets.addAll(cleanupScriptsProvider.getDescriptors(testClass));

      return allDataSets;
   }

   private void addResources(Archive<?> applicationArchive, final JavaArchive dataArchive)
   {
      if (JavaArchive.class.isInstance(applicationArchive))
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

   private void addSqlScriptsAsResource(Archive<?> applicationArchive, String[] scripts)
   {
      if (scripts == null)
      {
         return;
      }

      for (String script : scripts)
      {
         if (ScriptHelper.isSqlScriptFile(script))
         {
            addResources(applicationArchive, createArchiveWithResources(script));
         }
      }
   }

   private SqlScriptProvider<ApplyScriptAfter> createProviderForScriptsToBeAppliedAfterTest(TestClass testClass)
   {
      return SqlScriptProvider.forAnnotation(ApplyScriptAfter.class)
                              .usingConfiguration(configurationInstance.get())
                              .extractingMetadataUsing(new MetadataExtractor(testClass))
                              .namingFollows(new PrefixedScriptFileNamingStrategy("after-", "sql"))
                              .build(new ValueExtractor<ApplyScriptAfter>()
                              {
                                 @Override
                                 public String[] extract(ApplyScriptAfter toExtract)
                                 {
                                    if (toExtract == null)
                                    {
                                       return new String[0];
                                    }
                                    return toExtract.value();
                                 }
                              });
   }

   private SqlScriptProvider<ApplyScriptBefore> createProviderForScriptsToBeAppliedBeforeTest(TestClass testClass)
   {
      return SqlScriptProvider.forAnnotation(ApplyScriptBefore.class)
                              .usingConfiguration(configurationInstance.get())
                              .extractingMetadataUsing(new MetadataExtractor(testClass))
                              .namingFollows(new PrefixedScriptFileNamingStrategy("before-", "sql"))
                              .build(new ValueExtractor<ApplyScriptBefore>()
                              {
                                 @Override
                                 public String[] extract(ApplyScriptBefore toExtract)
                                 {
                                    if (toExtract == null)
                                    {
                                       return new String[0];
                                    }
                                    return toExtract.value();
                                 }
                              });
   }

   private SqlScriptProvider<CleanupUsingScript> createProviderForCleanupScripts(TestClass testClass)
   {
      return SqlScriptProvider.forAnnotation(CleanupUsingScript.class)
            .usingConfiguration(configurationInstance.get())
            .extractingMetadataUsing(new MetadataExtractor(testClass))
            .namingFollows(new PrefixedScriptFileNamingStrategy("cleanup-", "sql"))
            .build(new ValueExtractor<CleanupUsingScript>()
            {
               @Override
               public String[] extract(CleanupUsingScript toExtract)
               {
                  if (toExtract == null)
                  {
                     return new String[0];
                  }
                  return toExtract.value();
               }
            });
   }

}
