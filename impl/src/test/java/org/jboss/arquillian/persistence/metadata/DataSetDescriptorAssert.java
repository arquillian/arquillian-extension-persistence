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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fest.assertions.Assertions;
import org.fest.assertions.GroupAssert;
import org.jboss.arquillian.persistence.data.DataSetDescriptor;
import org.jboss.arquillian.persistence.data.Format;

public class DataSetDescriptorAssert extends GroupAssert<DataSetDescriptorAssert, Collection<DataSetDescriptor>>
{

   protected DataSetDescriptorAssert(Collection<DataSetDescriptor> actual)
   {
      super(DataSetDescriptorAssert.class, actual);
   }

   public static DataSetDescriptorAssert assertThat(DataSetDescriptor ... dataSetDescriptors)
   {
      return new DataSetDescriptorAssert(Arrays.asList(dataSetDescriptors));
   }
   
   public static DataSetDescriptorAssert assertThat(Collection<DataSetDescriptor> dataSetDescriptors)
   {
      return new DataSetDescriptorAssert(dataSetDescriptors);
   }

   public DataSetDescriptorAssert containsOnlyFollowingFiles(String ... files)
   {
      Assertions.assertThat(extractFileNames()).containsOnly(files);
      return this;
   }
   
   public DataSetDescriptorAssert containsOnlyFollowingFormats(Format ... formats)
   {
      Assertions.assertThat(extractFormats()).containsOnly(formats);
      return this;
   }
   
   private List<Format> extractFormats()
   {
      final List<Format> formats = new ArrayList<Format>();
      for (DataSetDescriptor dataSetDescriptor : actual)
      {
         formats.add(dataSetDescriptor.getFormat());
      }
      return formats ;
   }

   private List<String> extractFileNames()
   {
      final List<String> fileNames = new ArrayList<String>();
      for (DataSetDescriptor dataSetDescriptor : actual)
      {
         fileNames.add(dataSetDescriptor.getFileName());
      }
      return fileNames;
   }


   @Override
   protected int actualGroupSize()
   {
      return actual.size();
   }


}
