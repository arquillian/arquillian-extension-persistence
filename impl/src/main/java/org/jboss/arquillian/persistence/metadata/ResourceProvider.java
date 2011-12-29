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
package org.jboss.arquillian.persistence.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.descriptor.ResourceDescriptor;
import org.jboss.arquillian.persistence.exception.InvalidDataSetLocation;
import org.jboss.arquillian.test.spi.TestClass;

public abstract class ResourceProvider<T extends ResourceDescriptor<?>>
{

   protected final PersistenceConfiguration configuration;

   protected final MetadataExtractor metadataExtractor;

   protected final Class<? extends Annotation> resourceAnnotation;

   public ResourceProvider(Class<? extends Annotation> resourceAnnotation, PersistenceConfiguration configuration, MetadataExtractor metadataExtractor)
   {
      this.resourceAnnotation = resourceAnnotation;
      this.configuration = configuration;
      this.metadataExtractor = metadataExtractor;
   }

   /**
    * Returns all resources defined for this test class
    * including those defined on the test method level.
    *
    * @param testClass
    * @return
    */
   public Set<T> getDescriptors(TestClass testClass)
   {
      final Set<T> descriptors = new HashSet<T>();
      for (Method testMethod : testClass.getMethods(resourceAnnotation))
      {
         descriptors.addAll(getDescriptors(testMethod));
      }
      descriptors.addAll(obtainClassLevelDataSet(testClass.getAnnotation(resourceAnnotation)));
      return descriptors ;
   }

   public List<T> getDescriptors(Method testMethod)
   {
      final List<T> descriptors = new ArrayList<T>();
      for (String dataFileName : getResourceFileNames(testMethod))
      {
         T descriptor = createDescriptor(dataFileName);
         descriptors.add(descriptor);
      }

      return descriptors;
   }

   abstract List<String> getResourceFileNames(Method testMethod);

   protected abstract T createDescriptor(String dataFileName);

   protected abstract String defaultLocation();

   protected abstract String defaultFileName();

   protected List<T> obtainClassLevelDataSet(Annotation classLevelAnnotation)
   {
      if (classLevelAnnotation == null)
      {
         return Collections.emptyList();
      }

      final List<T> descriptors = new ArrayList<T>();

      try
      {
         String[] values = (String[]) classLevelAnnotation.annotationType()
                                                          .getMethod("value")
                                                          .invoke(classLevelAnnotation);
         final List<String> dataFileNames = new ArrayList<String>(Arrays.asList(values));
         if (dataFileNames.isEmpty() || dataFileNames.get(0).isEmpty())
         {
            String defaultFileName = defaultFileName();
            dataFileNames.clear();
            dataFileNames.add(defaultFileName);
         }

         for (String dataFileName : dataFileNames)
         {
            descriptors.add(createDescriptor(dataFileName));
         }

      }
      catch (Exception e)
      {
         throw new MetadataProcessingException("Unable to evaluate annotation value", e);
      }

      return descriptors;
   }

   protected String defaultFolder()
   {
      String defaultLocation = defaultLocation();
      if (!defaultLocation.endsWith("/"))
      {
         defaultLocation += "/";
      }
      return defaultLocation;
   }

   /**
    * Checks if data set file exists in the default location.
    * If that's not the case, file is looked up starting from the root.
    *
    * @return determined file location
    */
   protected String determineLocation(String location)
   {
      if (existsInDefaultLocation(location))
      {
         return defaultFolder() + location;
      }
      if (!existsInGivenLocation(location))
      {
         throw new InvalidDataSetLocation("Unable to locate " + location +
               ". File does not exist even in default location " + defaultLocation());
      }
      return location;
   }

   private boolean existsInGivenLocation(String location)
   {
      try
      {
         final URL url = load(location);
         if (url == null)
         {
            return false;
         }
      }
      catch (URISyntaxException e)
      {
         throw new InvalidDataSetLocation("Unable to open resource file", e);
      }

      return true;
   }

   private boolean existsInDefaultLocation(String location)
   {
      String defaultLocation = defaultFolder() + location;
      return existsInGivenLocation(defaultLocation);
   }

   private URL load(String resourceLocation) throws URISyntaxException
   {
      return Thread.currentThread().getContextClassLoader().getResource(resourceLocation);
   }

}
