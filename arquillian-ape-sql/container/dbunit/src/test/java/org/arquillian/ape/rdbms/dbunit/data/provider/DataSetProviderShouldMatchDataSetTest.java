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
package org.arquillian.ape.rdbms.dbunit.data.provider;

import org.arquillian.ape.rdbms.ShouldMatchDataSet;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.arquillian.ape.rdbms.core.data.descriptor.Format;
import org.arquillian.ape.rdbms.core.exception.InvalidResourceLocation;
import org.arquillian.ape.rdbms.core.exception.UnsupportedDataFormatException;
import org.arquillian.ape.rdbms.core.metadata.MetadataExtractor;
import org.arquillian.ape.rdbms.dbunit.configuration.DBUnitConfiguration;
import org.arquillian.ape.rdbms.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.arquillian.ape.testutils.DataSetDescriptorAssert;
import org.arquillian.ape.testutils.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DataSetProviderShouldMatchDataSetTest {

    private static final String DEFAULT_FILENAME_FOR_TEST_METHOD = "expected-" + ShouldMatchDataSetAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.xls";

    private static final String XML_EXPECTED_DATA_SET_ON_CLASS_LEVEL = "datasets/xml/expected-class-level.xml";

    private static final String XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL = "datasets/xml/expected-method-level.xml";

    private static final String EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL = "datasets/xls/expected-method-level.xls";

    private DBUnitConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultDBUnitConfiguration();

    @Test
    public void should_fetch_all_expected_data_sets_defined_for_test_class() throws Exception {
        // given
        TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
        ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        Collection<DataSetResourceDescriptor> dataSetDescriptors = dataSetProvider.getDescriptors(testEvent.getTestClass());

        // then
        DataSetDescriptorAssert.assertThat(dataSetDescriptors).containsOnlyFollowingFiles(XML_EXPECTED_DATA_SET_ON_CLASS_LEVEL,
                XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL, DEFAULT_FILENAME_FOR_TEST_METHOD, EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL, "one.xml", "two.xls", "three.yml");

    }

    @Test
    public void should_fetch_data_file_name_from_test_level_annotation() throws Exception {
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
    public void should_fetch_data_from_class_level_annotation_when_not_defined_for_test_method() throws Exception {
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
    public void should_fetch_data_format_from_method_level_annotation() throws Exception {
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
    public void should_infer_data_format_from_file_name_when_not_defined_on_method_level_annotation() throws Exception {
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
    public void should_infer_data_format_from_file_name_when_not_defined_on_class_level_annotation() throws Exception {
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
    public void should_throw_exception_when_format_cannot_be_infered_from_file_extension() throws Exception {
        // given
        TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotationWithUnsupportedFormat(), ShouldMatchDataSetAnnotationWithUnsupportedFormat.class.getMethod("shouldFailWithNonSupportedFileExtension"));
        ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        List<Format> expectedDataFormats = new ArrayList<Format>(dataSetProvider.getDataFormats(testEvent.getTestMethod()));

        // then
        // exception should be thrown
    }

    @Test
    public void should_provide_default_file_name_when_not_specified_in_annotation() throws Exception {
        // given
        String expectedFileName = DEFAULT_FILENAME_FOR_TEST_METHOD;
        TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
        ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        List<String> files = new ArrayList<String>(dataSetProvider.getResourceFileNames(testEvent.getTestMethod()));

        // then
        assertThat(files).containsOnly(expectedFileName);
    }

    @Test
    public void should_provide_default_file_name_when_not_specified_in_annotation_on_class_level() throws Exception {
        // given
        String expectedFileName = "expected-" + ShouldMatchDataSetAnnotatedOnClassLevelOnly.class.getName() + ".xls";
        TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotatedOnClassLevelOnly(), ShouldMatchDataSetAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
        ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        Collection<DataSetResourceDescriptor> files = dataSetProvider.getDescriptors(testEvent.getTestClass());

        // then
        assertThat(files).containsOnly(new DataSetResourceDescriptor(expectedFileName, Format.EXCEL));
    }

    @Test
    public void should_extract_all_data_set_files() throws Exception {
        // given
        DataSetResourceDescriptor xml = new DataSetResourceDescriptor("one.xml", Format.XML);
        DataSetResourceDescriptor xls = new DataSetResourceDescriptor("two.xls", Format.EXCEL);
        DataSetResourceDescriptor yml = new DataSetResourceDescriptor("three.yml", Format.YAML);
        TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotatedClass(), ShouldMatchDataSetAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
        ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        List<DataSetResourceDescriptor> dataSetDescriptors = new ArrayList<DataSetResourceDescriptor>(dataSetProvider.getDescriptorsDefinedFor(testEvent.getTestMethod()));

        // then
        assertThat(dataSetDescriptors).containsExactly(xml, xls, yml);
    }

    @Test(expected = InvalidResourceLocation.class)
    public void should_throw_exception_for_non_existing_file_infered_from_class_level_annotation() throws Exception {
        // given
        TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotatedOnClassLevelOnlyNonExistingFile(),
                ShouldMatchDataSetAnnotatedOnClassLevelOnlyNonExistingFile.class.getMethod("shouldFail"));
        ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        Collection<DataSetResourceDescriptor> dataSetDescriptors = dataSetProvider.getDescriptorsDefinedFor(testEvent.getTestMethod());

        // then
        // exception should be thrown
    }

    @Test(expected = InvalidResourceLocation.class)
    public void should_throw_exception_for_non_existing_file_defined_on_method_level_annotation() throws Exception {
        // given
        TestEvent testEvent = new TestEvent(new ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
                ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldFailForNonExistingFile"));
        ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        Collection<DataSetResourceDescriptor> dataSetDescriptors = dataSetProvider.getDescriptorsDefinedFor(testEvent.getTestMethod());

        // then
        // exception should be thrown
    }

    @Test
    public void should_find_file_in_default_location_if_not_specified_explicitly() throws Exception {
        // given
        DataSetResourceDescriptor expectedFile = new DataSetResourceDescriptor(defaultConfiguration.getDefaultDataSetLocation() + "/tables-in-datasets-folder.yml", Format.YAML);
        TestEvent testEvent = new TestEvent(new ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
                ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldPassForFileStoredInDefaultLocation"));
        ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        List<DataSetResourceDescriptor> dataSetDescriptors = new ArrayList<DataSetResourceDescriptor>(dataSetProvider.getDescriptorsDefinedFor(testEvent.getTestMethod()));

        // then
        assertThat(dataSetDescriptors).containsOnly(expectedFile);
    }

    // ----------------------------------------------------------------------------------------

    private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException {
        TestEvent testEvent = new TestEvent(new ShouldMatchDataSetAnnotatedClass(), ShouldMatchDataSetAnnotatedClass.class.getMethod(testMethod));
        return testEvent;
    }

    @UsingDataSet("datasets/test.xml")
    @ShouldMatchDataSet(XML_EXPECTED_DATA_SET_ON_CLASS_LEVEL)
    private static class ShouldMatchDataSetAnnotatedClass {
        public void shouldPassWithoutDataDefinedOnMethodLevel() {
        }

        @ShouldMatchDataSet(XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL)
        public void shouldPassWithDataButWithoutFormatDefinedOnMethodLevel() {
        }

        @ShouldMatchDataSet(value = EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL)
        public void shouldPassWithDataAndFormatDefinedOnMethodLevel() {
        }

        @ShouldMatchDataSet
        public void shouldPassWithDataFileNotSpecified() {
        }

        @ShouldMatchDataSet({"one.xml", "two.xls", "three.yml"})
        public void shouldPassWithMultipleFilesDefined() {
        }
    }

    private static class ShouldMatchDataSetAnnotationWithUnsupportedFormat {
        @ShouldMatchDataSet("arquillian.ike")
        public void shouldFailWithNonSupportedFileExtension() {
        }
    }

    @ShouldMatchDataSet
    private static class ShouldMatchDataSetAnnotatedOnClassLevelOnly {
        public void shouldPass() {
        }
    }

    @ShouldMatchDataSet
    private static class ShouldMatchDataSetAnnotatedOnClassLevelOnlyNonExistingFile {
        public void shouldFail() {
        }
    }

    private static class ShouldMatchDataSetOnTestMethodLevelWithNonExistingFileAndDefaultLocation {
        @ShouldMatchDataSet("non-existing.xml")
        public void shouldFailForNonExistingFile() {
        }

        @ShouldMatchDataSet("tables-in-datasets-folder.yml")
        public void shouldPassForFileStoredInDefaultLocation() {
        }

    }

}
