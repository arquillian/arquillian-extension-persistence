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

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.core.data.descriptor.DtdFileResourceDescriptor;
import org.jboss.arquillian.persistence.core.data.descriptor.Format;
import org.jboss.arquillian.persistence.core.data.descriptor.ResourceDescriptor;
import org.jboss.arquillian.persistence.core.metadata.AnnotationInspector;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionEnabler;
import org.jboss.arquillian.persistence.dbunit.api.CustomColumnFilter;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.jboss.arquillian.persistence.dbunit.data.provider.DataSetProvider;
import org.jboss.arquillian.persistence.dbunit.data.provider.ExpectedDataSetProvider;
import org.jboss.arquillian.persistence.dbunit.dataset.xml.DtdResolver;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.util.*;

import static org.jboss.arquillian.persistence.core.data.descriptor.Format.isFileType;

/**
 * Appends all data sets defined for the test class to the test archive.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DBUnitDataSetsTestArchiveEnricher implements ApplicationArchiveProcessor
{

   @Inject
   Instance<DBUnitConfiguration> dbunitConfigurationInstance;

   @Override
   public void process(Archive<?> applicationArchive, TestClass testClass)
   {

      final PersistenceExtensionEnabler persistenceExtensionEnabler = new PersistenceExtensionEnabler(testClass);
      if (!persistenceExtensionEnabler.shouldPersistenceExtensionBeActivated())
      {
         return;
      }

      addCustomColumnFilters(applicationArchive, testClass);
      addResources(applicationArchive, testClass);
   }

   private void addResources(Archive<?> applicationArchive, TestClass testClass)
   {
      final Set<ResourceDescriptor<?>> allDataResources = fetchAllDataResources(testClass);
      if (!allDataResources.isEmpty())
      {
         addResources(applicationArchive, allDataResources);
      }
   }

   // Private helper methods

   private void addCustomColumnFilters(Archive<?> applicationArchive, TestClass testClass)
   {
      final AnnotationInspector<CustomColumnFilter> inspector = new AnnotationInspector<CustomColumnFilter>(testClass, CustomColumnFilter.class);
      final Collection<CustomColumnFilter> allCustomAnnotations = inspector.fetchAll();
      if (allCustomAnnotations.isEmpty())
      {
         return;
      }

      if (applicationArchive instanceof EnterpriseArchive)
      {
         final JavaArchive customFiltersArchive = ShrinkWrap.create(JavaArchive.class, "arquillian-persistence-dbunit-custom-filters.jar");
         for (CustomColumnFilter filter : allCustomAnnotations)
         {
            customFiltersArchive.addClasses(filter.value());
            EnterpriseArchive.class.cast(applicationArchive).addAsLibrary(customFiltersArchive);
         }
      }
      else if (applicationArchive instanceof ClassContainer)
      {
         for (CustomColumnFilter filter : allCustomAnnotations)
         {
            (ClassContainer.class.cast(applicationArchive)).addClasses(filter.value());
         }
      }

   }

   private Set<ResourceDescriptor<?>> fetchAllDataResources(TestClass testClass)
   {
      final Set<ResourceDescriptor<?>> allDataSets = new HashSet<ResourceDescriptor<?>>();

      final DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testClass), dbunitConfigurationInstance.get());
      final ExpectedDataSetProvider expectedDataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testClass), dbunitConfigurationInstance.get());

      allDataSets.addAll(dataSetProvider.getDescriptors(testClass));
      allDataSets.addAll(expectedDataSetProvider.getDescriptors(testClass));
      allDataSets.addAll(extractDtds(dataSetProvider.getDescriptors(testClass)));
      allDataSets.addAll(extractDtds(expectedDataSetProvider.getDescriptors(testClass)));

      return allDataSets;
   }

   private void addResources(Archive<?> applicationArchive, Set<ResourceDescriptor<?>> allDataResources)
   {
      final List<String> resources = extractPaths(allDataResources);

      if (EnterpriseArchive.class.isInstance(applicationArchive))
      {
         ((EnterpriseArchive) applicationArchive).addAsLibrary(createArchiveWithResources(resources));
      }
      else if (ResourceContainer.class.isInstance(applicationArchive))
      {
         addResourcesToApplicationArchive((ResourceContainer<?>) applicationArchive, resources);
      }
      else
      {
         throw new RuntimeException("Unsupported archive type " + applicationArchive.getClass().getName());
      }
   }

   private void addResourcesToApplicationArchive(ResourceContainer<?> applicationArchive, List<String> resourcePaths)
   {
      for (String path : resourcePaths)
      {
         applicationArchive.addAsResource(path);
      }
   }

   private JavaArchive createArchiveWithResources(Collection<String> resourcePaths)
   {
      final JavaArchive dataSetsArchive = ShrinkWrap.create(JavaArchive.class, "arquillian-persistence-datasets.jar");

      for (String path : resourcePaths)
      {
         dataSetsArchive.addAsResource(path);
      }

      return dataSetsArchive;
   }

   private List<String> extractPaths(final Collection<? extends ResourceDescriptor<?>> descriptors)
   {
      final List<String> paths = new ArrayList<String>(descriptors.size());

      for (ResourceDescriptor<?> descriptor : descriptors)
      {
         if (isFileType(descriptor.getFormat()))
         {
            paths.add(descriptor.getLocation());
         }
      }

      return paths;
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
}
