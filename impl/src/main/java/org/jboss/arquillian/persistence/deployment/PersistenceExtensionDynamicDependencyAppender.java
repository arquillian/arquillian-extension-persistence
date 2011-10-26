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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.DataSetDescriptor;
import org.jboss.arquillian.persistence.metadata.DataSetProvider;
import org.jboss.arquillian.persistence.metadata.MetadataExtractor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 * Appends all data sets defined for the test class and resolves
 * all required dependencies to run persistence tests.
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
      resolveDependencies(applicationArchive, testClass);
      addDataSets(applicationArchive, testClass);
   }

   private void addDataSets(Archive<?> applicationArchive, TestClass testClass)
   {
      MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);
      JavaArchive dataSetsArchive = ShrinkWrap.create(JavaArchive.class);

      DataSetProvider dataSetProvider = new DataSetProvider(metadataExtractor, configuration.get());

      Set<DataSetDescriptor> allDataSets = new HashSet<DataSetDescriptor>();
      allDataSets.addAll(dataSetProvider.getDataSetDescriptors(testClass));
      allDataSets.addAll(dataSetProvider.getExpectedDataSetDescriptors(testClass));
      for (DataSetDescriptor dataSetDescriptor : allDataSets)
      {
         dataSetsArchive.addAsManifestResource(dataSetDescriptor.getFileName());
      }
      applicationArchive.merge(dataSetsArchive);
   }

   private void resolveDependencies(Archive<?> applicationArchive, TestClass testClass)
   {
      // TODO scan for formats
      Collection<GenericArchive> dependencies = DependencyResolvers.use(MavenDependencyResolver.class)
                                                             .artifact("org.dbunit:dbunit:2.4.8")
                                                             .exclusions("junit:junit")
                                                             .resolveAs(GenericArchive.class);
      for (Archive<?> archive : dependencies)
      {
         applicationArchive.merge(archive);
      }
   }

}
