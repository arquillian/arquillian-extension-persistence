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

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.DataSetDescriptor;
import org.jboss.arquillian.persistence.data.Format;
import org.jboss.arquillian.persistence.exception.UnsupportedDataFormatException;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class DataSetProviderDataTest
{

   private static final String DEFAULT_FILENAME_FOR_TEST_METHOD = DataAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.xls";

   private static final String XML_DATA_SET_ON_CLASS_LEVEL = "datasets/xml/class-level.xml";

   private static final String XML_DATA_SET_ON_METHOD_LEVEL = "datasets/xml/method-level.xml";
   
   private static final String EXCEL_DATA_SET_ON_METHOD_LEVEL = "datasets/xls/method-level.xls";

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldFetchAllDataSetsDefinedForTestClass() throws Exception
   {
      // given
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      Set<DataSetDescriptor> dataSetDescriptors = dataSetProvider.getDataSetDescriptors(testEvent.getTestClass());

      // then
      DataSetDescriptorAssert.assertThat(dataSetDescriptors).containsOnlyFollowingFiles(XML_DATA_SET_ON_CLASS_LEVEL, 
            XML_DATA_SET_ON_METHOD_LEVEL, DEFAULT_FILENAME_FOR_TEST_METHOD, EXCEL_DATA_SET_ON_METHOD_LEVEL, "one.xml", "two.xls", "three.yml");

   }
   
   @Test
   public void shouldFetchDataFileNameFromTestLevelAnnotation() throws Exception
   {
      // given
      String expectedDataFile = XML_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> dataFiles = dataSetProvider.getDataFileNames(testEvent.getTestMethod());

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }
   
   @Test
   public void shouldFetchDataFromClassLevelAnnotationWhenNotDefinedForTestMethod() throws Exception
   {
      // given
      String expectedDataFile = XML_DATA_SET_ON_CLASS_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithoutDataDefinedOnMethodLevel");
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> dataFiles = dataSetProvider.getDataFileNames(testEvent.getTestMethod());

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }
   
   @Test
   public void shouldFetchDataFormatFromMethodLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.EXCEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataAndFormatDefinedOnMethodLevel");
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<Format> dataFormats = dataSetProvider.getDataFormats(testEvent.getTestMethod());

      // then
      assertThat(dataFormats).containsOnly(expectedFormat);
   }
   
   @Test
   public void shouldInferDataFormatFromFileNameWhenNotDefinedOnMethodLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<Format> dataFormats = dataSetProvider.getDataFormats(testEvent.getTestMethod());

      // then
      assertThat(dataFormats).containsOnly(expectedFormat);
   }
   
   @Test
   public void shouldInferDataFormatFromFileNameWhenNotDefinedOnClassLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      TestEvent testEvent = createTestEvent("shouldPassWithoutDataDefinedOnMethodLevel");
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<Format> dataFormats = dataSetProvider.getDataFormats(testEvent.getTestMethod());

      // then
      assertThat(dataFormats).containsOnly(expectedFormat);
   }
   
   @Test(expected = UnsupportedDataFormatException.class)
   public void shouldThrowExceptionWhenFormatCannotBeInferedFromFileExtension() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new DataAnnotationWithUnsupportedFormat(), DataAnnotationWithUnsupportedFormat.class.getMethod("shouldFailWithNonSupportedFileExtension"));
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<Format> dataFormats = dataSetProvider.getDataFormats(testEvent.getTestMethod());

      // then
      // exception should be thrown      
   }
   
   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotation() throws Exception
   {
      // given
      String expectedFileName = DEFAULT_FILENAME_FOR_TEST_METHOD;
      TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> files = dataSetProvider.getDataFileNames(testEvent.getTestMethod());

      // then
      assertThat(files).containsOnly(expectedFileName);
   }
   
   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotationOnClassLevel() throws Exception
   {
      // given
      String expectedFileName = DataAnnotatedOnClassLevelOnly.class.getName() + ".xls";
      TestEvent testEvent = new TestEvent(new DataAnnotatedOnClassLevelOnly(), DataAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> files = dataSetProvider.getDataFileNames(testEvent.getTestMethod());

      // then
      assertThat(files).containsOnly(expectedFileName);
   }
   
   @Test
   public void shouldExtractAllDataSetFiles() throws Exception
   {
      // given
      DataSetDescriptor xml = new DataSetDescriptor("one.xml", Format.XML);
      DataSetDescriptor xls = new DataSetDescriptor("two.xls", Format.EXCEL);
      DataSetDescriptor yml = new DataSetDescriptor("three.yml", Format.YAML);
      TestEvent testEvent = new TestEvent(new DataAnnotatedClass(), DataAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
      DataSetProvider dataSetProvider = new DataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<DataSetDescriptor> dataSetDescriptors = dataSetProvider.getDataSetDescriptors(testEvent.getTestMethod());
      
      // then
      assertThat(dataSetDescriptors).containsExactly(xml, xls, yml);
   }
   
   // ---------------------------------------------------------------------------------------- 
   
   private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException
   {
      TestEvent testEvent = new TestEvent(new DataAnnotatedClass(), DataAnnotatedClass.class.getMethod(testMethod));
      return testEvent;
   }
   
   @Data(XML_DATA_SET_ON_CLASS_LEVEL)
   private static class DataAnnotatedClass
   {
      public void shouldPassWithoutDataDefinedOnMethodLevel() {}

      @Data(XML_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataButWithoutFormatDefinedOnMethodLevel () {}
      
      @Data(value = EXCEL_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataAndFormatDefinedOnMethodLevel() {}
      
      @Data
      public void shouldPassWithDataFileNotSpecified() {}
      
      @Data({"one.xml", "two.xls", "three.yml"})
      public void shouldPassWithMultipleFilesDefined() {}
   }
   
   private static class DataAnnotationWithUnsupportedFormat
   {
      @Data("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
   }
   
   @Data
   private static class DataAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }
   
}
