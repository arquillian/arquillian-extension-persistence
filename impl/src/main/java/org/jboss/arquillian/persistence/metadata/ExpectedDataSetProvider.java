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

import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.descriptor.DataSetDescriptor;
import org.jboss.arquillian.persistence.data.descriptor.Format;
import org.jboss.arquillian.persistence.data.naming.ExpectedDataSetFileNamingStrategy;
import org.jboss.arquillian.persistence.exception.UnsupportedDataFormatException;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class ExpectedDataSetProvider extends ResourceProvider<DataSetDescriptor>
{

   public ExpectedDataSetProvider(MetadataExtractor metadataExtractor, PersistenceConfiguration configuration)
   {
      super(ShouldMatchDataSet.class, configuration, metadataExtractor);
   }

   @Override
   protected DataSetDescriptor createDescriptor(String dataFileName)
   {
      return new DataSetDescriptor(determineLocation(dataFileName), inferFormat(dataFileName));
   }

   @Override
   protected String defaultLocation()
   {
      return configuration.getDefaultDataSetLocation();
   }

   @Override
   protected String defaultFileName()
   {
      Format format = configuration.getDefaultDataSetFormat();
      String defaultFileName = new ExpectedDataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass());
      return defaultFileName;
   }

   @Override
   List<String> getResourceFileNames(Method testMethod)
   {
      ShouldMatchDataSet dataAnnotation = getResourceAnnotation(testMethod);
      String[] specifiedFileNames = dataAnnotation.value();
      if (specifiedFileNames.length == 0 || "".equals(specifiedFileNames[0].trim()))
      {
         return Arrays.asList(getDefaultFileName(testMethod));
      }
      return Arrays.asList(specifiedFileNames);
   }


   // Internals

   private Format inferFormat(String dataFileName)
   {
      Format format = Format.inferFromFile(dataFileName);
      if (Format.UNSUPPORTED.equals(format))
      {
         throw new UnsupportedDataFormatException("File " + dataFileName + " is not supported.");
      }
      return format;
   }

   List<Format> getDataFormats(Method testMethod)
   {
      final List<Format> formats = new ArrayList<Format>();
      for (String dataFileName : getResourceFileNames(testMethod))
      {
         formats.add(inferFormat(dataFileName));
      }
      return formats;
   }

   private ShouldMatchDataSet getResourceAnnotation(Method testMethod)
   {
      return metadataExtractor.shouldMatchDataSet().getUsingPrecedence(testMethod);
   }

   private String getDefaultFileName(Method testMethod)
   {
      Format format = configuration.getDefaultDataSetFormat();

      if (metadataExtractor.shouldMatchDataSet().isDefinedOn(testMethod))
      {
         return new ExpectedDataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass(), testMethod);
      }

      return new ExpectedDataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass());
   }

}
