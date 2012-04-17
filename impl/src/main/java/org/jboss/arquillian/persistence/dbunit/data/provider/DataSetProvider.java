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
package org.jboss.arquillian.persistence.dbunit.data.provider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.persistence.core.data.provider.ResourceProvider;
import org.jboss.arquillian.persistence.core.exception.UnsupportedDataFormatException;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.jboss.arquillian.persistence.dbunit.data.descriptor.Format;
import org.jboss.arquillian.persistence.dbunit.data.naming.DataSetFileNamingStrategy;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DataSetProvider extends ResourceProvider<DataSetResourceDescriptor>
{

   private final DBUnitConfiguration configuration;

   public DataSetProvider(MetadataExtractor metadataExtractor, DBUnitConfiguration configuration)
   {
      super(UsingDataSet.class, metadataExtractor);
      this.configuration = configuration;
   }

   @Override
   protected DataSetResourceDescriptor createDescriptor(String dataFileName)
   {
      return new DataSetResourceDescriptor(determineLocation(dataFileName), inferFormat(dataFileName));
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
      String defaultFileName = new DataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass());
      return defaultFileName;
   }

   @Override
   public Collection<String> getResourceFileNames(Method testMethod)
   {
      UsingDataSet dataAnnotation = getResourceAnnotation(testMethod);
      String[] specifiedFileNames = dataAnnotation.value();
      if (specifiedFileNames.length == 0 || "".equals(specifiedFileNames[0].trim()))
      {
         return Arrays.asList(getDefaultFileName(testMethod));
      }
      return Arrays.asList(specifiedFileNames);
   }

   // Private methods

   private Format inferFormat(String dataFileName)
   {
      Format format = Format.inferFromFile(dataFileName);
      if (Format.UNSUPPORTED.equals(format))
      {
         throw new UnsupportedDataFormatException("File " + dataFileName + " is not supported as data set format.");
      }
      return format;
   }

   Collection<Format> getDataFormats(Method testMethod)
   {
      final List<Format> formats = new ArrayList<Format>();
      for (String dataFileName : getResourceFileNames(testMethod))
      {
         formats.add(inferFormat(dataFileName));
      }
      return formats;
   }

   private UsingDataSet getResourceAnnotation(Method testMethod)
   {
      return metadataExtractor.usingDataSet().fetchUsingFirst(testMethod);
   }

   private String getDefaultFileName(Method testMethod)
   {
      Format format = configuration.getDefaultDataSetFormat();

      if (metadataExtractor.usingDataSet().isDefinedOn(testMethod))
      {
         return new DataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass(), testMethod);
      }

      return new DataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass());
   }

}
