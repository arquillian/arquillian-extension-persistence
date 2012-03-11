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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.descriptor.DataSetResourceDescriptor;
import org.jboss.arquillian.persistence.data.descriptor.Format;
import org.jboss.arquillian.persistence.exception.InvalidDataSetLocation;
import org.jboss.arquillian.persistence.exception.UnsupportedDataFormatException;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class DataSetProviderShouldMatchDataSetTest
{

   private static final String DEFAULT_FILENAME_FOR_TEST_METHOD = "expected-" + ShouldMatchDataSetAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.xls";

   private static final String XML_EXPECTED_DATA_SET_ON_CLASS_LEVEL = "datasets/xml/expected-class-level.xml";

   private static final String XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL = "datasets/xml/expected-method-level.xml";

   private static final String EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL = "datasets/xls/expected-method-level.xls";

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldFetchAllExpectedDataSetsDefinedForTestClass() throws Exception
   {
      // given
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      Set<DataSetResourceDescriptor> dataSetDescriptors = dataSetProvider.getDescriptors(testEvent.getTestClass());

      // then
      DataSetDescriptorAssert.assertThat(dataSetDescriptors).containsOnlyFollowingFiles(XML_EXPECTED_DATA_SET_ON_CLASS_LEVEL,
            XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL, DEFAULT_FILENAME_FOR_TEST_METHOD, EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL, "one.xml", "two.xls", "three.yml");

   }

   @Test
   public void shouldFetchDataFileNameFromTestLevelAnnotation() throws Exception
   {
      // given
      String expectedDataFile = XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> dataFiles = new ArrayList<String>(dataSetProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }

   @Test
   public void shouldFetchDataFromClassLevelAnnotationWhenNotDefinedForTestMethod() throws Exception
   {
      // given
      String expectedDataFile = XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> dataFiles = new ArrayList<String>(dataSetProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }

   @Test
   public void shouldFetchDataFormatFromMethodLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.EXCEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataAndFormatDefinedOnMethodLevel");
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

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
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

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
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<Format> dataFormats = dataSetProvider.getDataFormats(testEvent.getTestMethod());

      // then
      assertThat(dataFormats).containsOnly(expectedFormat);
   }

   @Test(expected = UnsupportedDataFormatException.class)
   public void shouldThrowExceptionWhenFormatCannotBeInferedFromFileExtension() throws Exception
   {
      // given
      TestEvent testEvent= new TestEvent(new ShouldMatchDataSetAnnotationWithUnsupportedFormat(), ShouldMatchDataSetAnnotationWithUnsupportedFormat.class.getMethod("shouldFailWithNonSupportedFileExtension"));
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<Format> expectedDataFormats =  new ArrayList<Format>(dataSetProvider.getDataFormats(testEvent.getTestMethod()));

      // then
      // exception should be thrown
   }

   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotation() throws Exception
   {
      // given
      String expectedFileName = DEFAULT_FILENAME_FOR_TEST_METHOD;
      TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> files =  new ArrayList<String>(dataSetProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotationOnClassLevel() throws Exception
   {
      // given
      String expectedFileName = "expected-" + ShouldMatchDataSetAnnotatedOnClassLevelOnly.class.getName() + ".xls";
      TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotatedOnClassLevelOnly(), ShouldMatchDataSetAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      Set<DataSetResourceDescriptor> files = dataSetProvider.getDescriptors(testEvent.getTestClass());

      // then
      assertThat(files).containsOnly(new DataSetResourceDescriptor(expectedFileName, Format.EXCEL));
   }

   @Test
   public void shouldExtractAllDataSetFiles() throws Exception
   {
      // given
      DataSetResourceDescriptor xml = new DataSetResourceDescriptor("one.xml", Format.XML);
      DataSetResourceDescriptor xls = new DataSetResourceDescriptor("two.xls", Format.EXCEL);
      DataSetResourceDescriptor yml = new DataSetResourceDescriptor("three.yml", Format.YAML);
      TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotatedClass(), ShouldMatchDataSetAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<DataSetResourceDescriptor> dataSetDescriptors = new ArrayList<DataSetResourceDescriptor>(dataSetProvider.getDescriptors(testEvent.getTestMethod()));

      // then
      assertThat(dataSetDescriptors).containsExactly(xml, xls, yml);
   }

   @Test(expected = InvalidDataSetLocation.class)
   public void shouldThrowExceptionForNonExistingFileInferedFromClassLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotatedOnClassLevelOnlyNonExistingFile(),
            ShouldMatchDataSetAnnotatedOnClassLevelOnlyNonExistingFile.class.getMethod("shouldFail"));
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      Collection<DataSetResourceDescriptor> dataSetDescriptors = dataSetProvider.getDescriptors(testEvent.getTestMethod());

      // then
      // exception should be thrown
   }

   @Test(expected = InvalidDataSetLocation.class)
   public void shouldThrowExceptionForNonExistingFileDefinedOnMethodLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldFailForNonExistingFile"));
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      Collection<DataSetResourceDescriptor> dataSetDescriptors = dataSetProvider.getDescriptors(testEvent.getTestMethod());

      // then
      // exception should be thrown
   }

   @Test
   public void shouldFindFileInDefaultLocationIfNotSpecifiedExplicitly() throws Exception
   {
      // given
      DataSetResourceDescriptor expectedFile = new DataSetResourceDescriptor(defaultConfiguration.getDefaultDataSetLocation() + "/tables-in-datasets-folder.yml", Format.YAML);
      TestEvent testEvent = new TestEvent(new ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldPassForFileStoredInDefaultLocation"));
      ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<DataSetResourceDescriptor> dataSetDescriptors =  new ArrayList<DataSetResourceDescriptor>(dataSetProvider.getDescriptors(testEvent.getTestMethod()));

      // then
      assertThat(dataSetDescriptors).containsOnly(expectedFile);
   }

   // ----------------------------------------------------------------------------------------

   private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException
   {
      TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotatedClass(), ShouldMatchDataSetAnnotatedClass.class.getMethod(testMethod));
      return testEvent;
   }

   @UsingDataSet("datasets/test.xml")
   @ShouldMatchDataSet(XML_EXPECTED_DATA_SET_ON_CLASS_LEVEL)
   private static class ShouldMatchDataSetAnnotatedClass
   {
      public void shouldPassWithoutDataDefinedOnMethodLevel() {}

      @ShouldMatchDataSet(XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataButWithoutFormatDefinedOnMethodLevel () {}

      @ShouldMatchDataSet(value = EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataAndFormatDefinedOnMethodLevel() {}

      @ShouldMatchDataSet
      public void shouldPassWithDataFileNotSpecified() {}

      @ShouldMatchDataSet({"one.xml", "two.xls", "three.yml"})
      public void shouldPassWithMultipleFilesDefined() {}
   }

   private static class ShouldMatchDataSetAnnotationWithUnsupportedFormat
   {
      @ShouldMatchDataSet("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
   }

   @ShouldMatchDataSet
   private static class ShouldMatchDataSetAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }

   @ShouldMatchDataSet
   private static class ShouldMatchDataSetAnnotatedOnClassLevelOnlyNonExistingFile
   {
      public void shouldFail() {}
   }

   private static class ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation
   {
      @ShouldMatchDataSet("non-existing.xml")
      public void shouldFailForNonExistingFile() {}

      @ShouldMatchDataSet("tables-in-datasets-folder.yml")
      public void shouldPassForFileStoredInDefaultLocation() {}

   }

}
