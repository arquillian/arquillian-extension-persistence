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
package org.jboss.arquillian.persistence.metadata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.DataSetDescriptor;
import org.jboss.arquillian.persistence.data.DataSetFileNamingStrategy;
import org.jboss.arquillian.persistence.data.ExpectedDataSetFileNamingStrategy;
import org.jboss.arquillian.persistence.data.Format;
import org.jboss.arquillian.persistence.exception.UnsupportedDataFormatException;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DataSetProvider
{

   private final PersistenceConfiguration configuration;

   private final MetadataExtractor metadataExtractor;
   
   private final Method testMethod;

   public DataSetProvider(Method testMethod, MetadataExtractor metadataExtractor, PersistenceConfiguration configuration)
   {
      this.metadataExtractor = metadataExtractor;
      this.configuration = configuration;
      this.testMethod = testMethod;
   }
   
   public List<DataSetDescriptor> getDataSetDescriptors()
   {
      final List<DataSetDescriptor> dataSetDescriptors = new ArrayList<DataSetDescriptor>();
      for (String dataFileName : getDataFileNames())
      {
         DataSetDescriptor dataSetDescriptor = new DataSetDescriptor(dataFileName, inferFormat(dataFileName));
         dataSetDescriptors.add(dataSetDescriptor);
      }
      
      return dataSetDescriptors;
   }
   
   public List<DataSetDescriptor> getExpectedDataSetDescriptors()
   {
      final List<DataSetDescriptor> dataSetDescriptors = new ArrayList<DataSetDescriptor>();
      for (String dataFileName : getExpectedDataFileNames())
      {
         DataSetDescriptor dataSetDescriptor = new DataSetDescriptor(dataFileName, inferFormat(dataFileName));
         dataSetDescriptors.add(dataSetDescriptor);
      }
      
      return dataSetDescriptors;
   }
   
   List<Format> getDataFormats()
   {
      final List<Format> formats = new ArrayList<Format>();
      for (String dataFileName : getDataFileNames())
      {
         formats.add(inferFormat(dataFileName));
      }
      return formats;
   }
   
   private Format inferFormat(String dataFileName)
   {
      Format format = Format.inferFromFile(dataFileName);
      if (Format.UNSUPPORTED.equals(format))
      {
         throw new UnsupportedDataFormatException("File " + getDataFileNames() + " is not supported.");
      }
      return format;
   }
   
   List<String> getDataFileNames()
   {
      Data dataAnnotation = getDataAnnotation();
      String[] specifiedFileNames = dataAnnotation.value();
      if (specifiedFileNames.length == 0 || "".equals(specifiedFileNames[0].trim()))
      {
         return Arrays.asList(getDefaultNamingForDataSetFile());
      }
      return Arrays.asList(specifiedFileNames);
   }
   
   List<Format> getExpectedDataFormats()
   {
      final List<Format> formats = new ArrayList<Format>();
      for (String dataFileName : getExpectedDataFileNames())
      {
         formats.add(inferFormat(dataFileName));
      }
      return formats;
   }

   List<String> getExpectedDataFileNames()
   {
      Expected expectedAnnotation = getExpectedAnnotation();
      String[] specifiedFileNames = expectedAnnotation.value();
      if (specifiedFileNames.length == 0 || "".equals(specifiedFileNames[0].trim()))
      {
         return Arrays.asList(getDefaultNamingForExpectedDataSetFile());
      }
      return Arrays.asList(specifiedFileNames);
   }
   
   private String getDefaultNamingForDataSetFile()
   {
      Format format = configuration.getDefaultDataSetFormat();
      
      if (metadataExtractor.hasDataAnnotationOn(testMethod))
      {
         return new DataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass(), testMethod);
      }
      
      return new DataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass());
   }

   
   private String getDefaultNamingForExpectedDataSetFile()
   {
      Format format = configuration.getDefaultDataSetFormat();

      if (metadataExtractor.hasExpectedAnnotationOn(testMethod))
      {
         return new ExpectedDataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass(), testMethod);
      }

      return new ExpectedDataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass());
   }
   
   private Data getDataAnnotation()
   {
      Data usedAnnotation = metadataExtractor.getDataAnnotationOnClassLevel();
      if (metadataExtractor.hasDataAnnotationOn(testMethod))
      {
         usedAnnotation = metadataExtractor.getDataAnnotationOn(testMethod);
      }

      return usedAnnotation;
   }
   
   private Expected getExpectedAnnotation()
   {
      Expected usedAnnotation = metadataExtractor.getExpectedAnnotationOnClassLevel();
      if (metadataExtractor.hasExpectedAnnotationOn(testMethod))
      {
         usedAnnotation = metadataExtractor.getExpectedAnnotationOn(testMethod);
      }
      
      return usedAnnotation;
   }

   
}
