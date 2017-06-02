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
package org.arquillian.ape.rdbms.script.data.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.arquillian.ape.rdbms.CleanupUsingScript;
import org.arquillian.ape.api.TestExecutionPhase;
import org.arquillian.ape.rdbms.core.exception.InvalidResourceLocation;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.arquillian.ape.rdbms.script.data.descriptor.FileSqlScriptResourceDescriptor;
import org.arquillian.ape.rdbms.script.data.descriptor.SqlScriptResourceDescriptor;
import org.arquillian.ape.rdbms.testutils.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlScriptProviderForCleanupScriptsTest {

    private static final String DEFAULT_FILENAME_FOR_TEST_METHOD =
        "cleanup-" + CleanupUsingScriptAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.sql";

    private static final String SQL_DATA_SET_ON_CLASS_LEVEL = "scripts/class-level.sql";

    private static final String SQL_DATA_SET_ON_METHOD_LEVEL = "scripts/method-level.sql";

    private ScriptingConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultScriptingConfiguration();

    private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException {
        TestEvent testEvent = new TestEvent(new CleanupUsingScriptAnnotatedClass(),
            CleanupUsingScriptAnnotatedClass.class.getMethod(testMethod));
        return testEvent;
    }

    @Test
    public void should_fetch_all_scripts_defined_for_test_class() throws Exception {
        // given
        TestEvent testEvent = createTestEvent("shouldPassWithDataFileButWithoutFormatDefinedOnMethodLevel");
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        Collection<SqlScriptResourceDescriptor> scriptDescriptors =
            scriptsProvider.getDescriptors(testEvent.getTestClass());

        // then
        SqlScriptDescriptorAssert.assertThat(scriptDescriptors).containsOnlyFollowingFiles(SQL_DATA_SET_ON_CLASS_LEVEL,
            SQL_DATA_SET_ON_METHOD_LEVEL, DEFAULT_FILENAME_FOR_TEST_METHOD, "one.sql", "two.sql", "three.sql");
    }

    @Test
    public void should_fetch_data_file_name_from_test_level_annotation() throws Exception {
        // given
        String expectedDataFile = SQL_DATA_SET_ON_METHOD_LEVEL;
        TestEvent testEvent = createTestEvent("shouldPassWithDataFileButWithoutFormatDefinedOnMethodLevel");
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        List<String> dataFiles = new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

        // then
        assertThat(dataFiles).containsOnly(expectedDataFile);
    }

    @Test
    public void should_fetch_data_from_class_level_annotation_when_not_defined_for_test_method() throws Exception {
        // given
        String expectedDataFile = SQL_DATA_SET_ON_CLASS_LEVEL;
        TestEvent testEvent = createTestEvent("shouldPassWithoutDataDefinedOnMethodLevel");
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        List<String> dataFiles = new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

        // then
        assertThat(dataFiles).containsOnly(expectedDataFile);
    }

    @Test
    public void should_provide_default_file_name_when_not_specified_in_annotation() throws Exception {
        // given
        String expectedFileName = DEFAULT_FILENAME_FOR_TEST_METHOD;
        TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        List<String> files = new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

        // then
        assertThat(files).containsOnly(expectedFileName);
    }

    @Test
    public void should_provide_default_file_name_when_not_specified_in_annotation_on_class_level() throws Exception {
        // given
        String expectedFileName = "cleanup-" + CleanupUsingScriptAnnotatedOnClassLevelOnly.class.getName() + ".sql";
        TestEvent testEvent = new TestEvent(new CleanupUsingScriptAnnotatedOnClassLevelOnly(),
            CleanupUsingScriptAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        List<String> files = new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

        // then
        assertThat(files).containsOnly(expectedFileName);
    }

    @Test
    public void should_extract_all_scirpts() throws Exception {
        // given
        FileSqlScriptResourceDescriptor one =
            new FileSqlScriptResourceDescriptor("one.sql", defaultConfiguration.getCharset());
        FileSqlScriptResourceDescriptor two =
            new FileSqlScriptResourceDescriptor("two.sql", defaultConfiguration.getCharset());
        FileSqlScriptResourceDescriptor three =
            new FileSqlScriptResourceDescriptor("three.sql", defaultConfiguration.getCharset());
        TestEvent testEvent = new TestEvent(new CleanupUsingScriptAnnotatedClass(),
            CleanupUsingScriptAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        List<SqlScriptResourceDescriptor> scriptDescriptors = new ArrayList<SqlScriptResourceDescriptor>(
            scriptsProvider.getDescriptorsDefinedFor(testEvent.getTestMethod()));

        // then
        assertThat(scriptDescriptors).containsExactly(one, two, three);
    }

    @Test(expected = InvalidResourceLocation.class)
    public void should_throw_exception_for_non_existing_file_infered_from_class_level_annotation() throws Exception {
        // given
        TestEvent testEvent = new TestEvent(new CleanupUsingScriptAnnotatedOnClassLevelOnlyNonExistingFile(),
            CleanupUsingScriptAnnotatedOnClassLevelOnlyNonExistingFile.class.getMethod("shouldFail"));
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        Collection<SqlScriptResourceDescriptor> scriptDescriptors =
            scriptsProvider.getDescriptorsDefinedFor(testEvent.getTestMethod());

        // then
        // exception should be thrown
    }

    @Test(expected = InvalidResourceLocation.class)
    public void should_throw_exception_for_non_existing_file_defined_on_method_level_annotation() throws Exception {
        // given
        TestEvent testEvent =
            new TestEvent(new CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
                CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod(
                    "shouldFailForNonExistingFile"));
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        Collection<SqlScriptResourceDescriptor> scriptDescriptors =
            scriptsProvider.getDescriptorsDefinedFor(testEvent.getTestMethod());

        // then
        // exception should be thrown
    }

    // ----------------------------------------------------------------------------------------

    @Test
    public void should_find_file_in_default_location_if_not_specified_explicitly() throws Exception {
        // given
        FileSqlScriptResourceDescriptor expectedFile = new FileSqlScriptResourceDescriptor(
            defaultConfiguration.getDefaultSqlScriptLocation() + "/tables-in-scripts-folder.sql",
            defaultConfiguration.getCharset());
        TestEvent testEvent =
            new TestEvent(new CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
                CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod(
                    "shouldPassForFileStoredInDefaultLocation"));
        SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        List<SqlScriptResourceDescriptor> dataSetDescriptors = new ArrayList<SqlScriptResourceDescriptor>(
            scriptsProvider.getDescriptorsDefinedFor(testEvent.getTestMethod()));

        // then
        assertThat(dataSetDescriptors).containsOnly(expectedFile);
    }

    private SqlScriptProvider<CleanupUsingScript> createSqlScriptProviderFor(TestEvent testEvent) {
        return SqlScriptProvider.createProviderForCleanupScripts(testEvent.getTestClass(), defaultConfiguration);
    }

    @CleanupUsingScript(SQL_DATA_SET_ON_CLASS_LEVEL)
    private static class CleanupUsingScriptAnnotatedClass {
        public void shouldPassWithoutDataDefinedOnMethodLevel() {
        }

        @CleanupUsingScript(SQL_DATA_SET_ON_METHOD_LEVEL)
        public void shouldPassWithDataFileButWithoutFormatDefinedOnMethodLevel() {
        }

        @CleanupUsingScript
        public void shouldPassWithDataFileNotSpecified() {
        }

        @CleanupUsingScript(phase = TestExecutionPhase.NONE)
        public void shouldNotInferCleanupFileWhenPhaseDefinedToNone() {
        }

        @CleanupUsingScript({"one.sql", "two.sql", "three.sql"})
        public void shouldPassWithMultipleFilesDefined() {
        }
    }

    private static class CleanupUsingScriptAnnotationWithUnsupportedFormat {
        @CleanupUsingScript("arquillian.ike")
        public void shouldFailWithNonSupportedFileExtension() {
        }
    }

    @CleanupUsingScript
    private static class CleanupUsingScriptAnnotatedOnClassLevelOnly {
        public void shouldPass() {
        }
    }

    @CleanupUsingScript
    private static class CleanupUsingScriptAnnotatedOnClassLevelOnlyNonExistingFile {
        public void shouldFail() {
        }
    }

    private static class CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation {
        @CleanupUsingScript("non-existing.sql")
        public void shouldFailForNonExistingFile() {
        }

        @CleanupUsingScript("tables-in-scripts-folder.sql")
        public void shouldPassForFileStoredInDefaultLocation() {
        }
    }
}
