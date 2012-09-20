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
package org.jboss.arquillian.persistence.core.data.provider;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.core.configuration.TestConfigurationLoader;
import org.jboss.arquillian.persistence.core.data.descriptor.FileSqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.core.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.core.exception.InvalidResourceLocation;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class SqlScriptProviderForScriptsAppliedBeforeTestMethodTest
{

   private static final String DEFAULT_FILENAME_FOR_TEST_METHOD = "before-" + ApplyScriptBeforeAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.sql";

   private static final String SQL_DATA_SET_ON_CLASS_LEVEL = "scripts/class-level.sql";

   private static final String SQL_DATA_SET_ON_METHOD_LEVEL = "scripts/method-level.sql";


   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void should_fetch_all_scripts_defined_for_test_class() throws Exception
   {
      // given
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      Set<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestClass());

      // then
      SqlScriptDescriptorAssert.assertThat(scriptDescriptors).containsOnlyFollowingFiles(SQL_DATA_SET_ON_CLASS_LEVEL,
            SQL_DATA_SET_ON_METHOD_LEVEL, DEFAULT_FILENAME_FOR_TEST_METHOD, "one.sql", "two.sql", "three.sql");

   }

   @Test
   public void should_fetch_data_file_name_from_test_level_annotation() throws Exception
   {
      // given
      String expectedDataFile = SQL_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> dataFiles = new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }

   @Test
   public void should_fetch_data_from_class_level_annotation_when_not_defined_for_test_method() throws Exception
   {
      // given
      String expectedDataFile = SQL_DATA_SET_ON_CLASS_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithoutDataDefinedOnMethodLevel");
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> dataFiles = new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }

   @Test
   public void should_provide_default_file_name_when_not_specified_in_annotation() throws Exception
   {
      // given
      String expectedFileName = DEFAULT_FILENAME_FOR_TEST_METHOD;
      TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> files =  new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void should_provide_default_file_name_when_not_specified_in_annotation_on_class_level() throws Exception
   {
      // given
      String expectedFileName = "before-" + ApplyScriptBeforeAnnotatedOnClassLevelOnly.class.getName() + ".sql";
      TestEvent testEvent = new TestEvent(new ApplyScriptBeforeAnnotatedOnClassLevelOnly(), ApplyScriptBeforeAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> files =  new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void should_extract_all_scripts() throws Exception
   {
      // given
      FileSqlScriptResourceDescriptor one = new FileSqlScriptResourceDescriptor("one.sql");
      FileSqlScriptResourceDescriptor two = new FileSqlScriptResourceDescriptor("two.sql");
      FileSqlScriptResourceDescriptor three = new FileSqlScriptResourceDescriptor("three.sql");
      TestEvent testEvent = new TestEvent(new ApplyScriptBeforeAnnotatedClass(), ApplyScriptBeforeAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> scriptDescriptors =  new ArrayList<SqlScriptResourceDescriptor>(scriptsProvider.getDescriptorsDefinedFor(testEvent.getTestMethod()));

      // then
      assertThat(scriptDescriptors).containsExactly(one, two, three);
   }

   @Test(expected = InvalidResourceLocation.class)
   public void should_throw_exception_for_non_existing_file_infered_from_class_level_annotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new UsingScriptAnnotatedOnClassLevelOnlyNonExistingFile(),
            UsingScriptAnnotatedOnClassLevelOnlyNonExistingFile.class.getMethod("shouldFail"));
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> scriptDescriptors = new ArrayList<SqlScriptResourceDescriptor>(scriptsProvider.getDescriptorsDefinedFor(testEvent.getTestMethod()));

      // then
      // exception should be thrown
   }

   @Test(expected = InvalidResourceLocation.class)
   public void should_throw_exception_for_non_existing_file_defined_on_method_level_annotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldFailForNonExistingFile"));
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> scriptDescriptors = new ArrayList<SqlScriptResourceDescriptor>(scriptsProvider.getDescriptorsDefinedFor(testEvent.getTestMethod()));

      // then
      // exception should be thrown
   }

   @Test
   public void should_find_file_in_default_location_if_not_specified_explicitly() throws Exception
   {
      // given
      FileSqlScriptResourceDescriptor expectedFile = new FileSqlScriptResourceDescriptor(defaultConfiguration.getDefaultSqlScriptLocation() + "/tables-in-scripts-folder.sql");
      TestEvent testEvent = new TestEvent(new UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldPassForFileStoredInDefaultLocation"));
      SqlScriptProvider<ApplyScriptBefore> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> dataSetDescriptors = new ArrayList<SqlScriptResourceDescriptor>(scriptsProvider.getDescriptorsDefinedFor(testEvent.getTestMethod()));

      // then
      assertThat(dataSetDescriptors).containsOnly(expectedFile);
   }

   // ----------------------------------------------------------------------------------------

   private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException
   {
      TestEvent testEvent = new TestEvent(new ApplyScriptBeforeAnnotatedClass(), ApplyScriptBeforeAnnotatedClass.class.getMethod(testMethod));
      return testEvent;
   }

   private SqlScriptProvider<ApplyScriptBefore> createSqlScriptProviderFor(TestEvent testEvent)
   {
      return SqlScriptProvider.createProviderForScriptsToBeAppliedBeforeTest(testEvent.getTestClass(), defaultConfiguration);
   }

   @ApplyScriptBefore(SQL_DATA_SET_ON_CLASS_LEVEL)
   private static class ApplyScriptBeforeAnnotatedClass
   {
      public void shouldPassWithoutDataDefinedOnMethodLevel() {}

      @ApplyScriptBefore(SQL_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataButWithoutFormatDefinedOnMethodLevel () {}

      @ApplyScriptBefore
      public void shouldPassWithDataFileNotSpecified() {}

      @ApplyScriptBefore({"one.sql", "two.sql", "three.sql"})
      public void shouldPassWithMultipleFilesDefined() {}

   }

   private static class UsingScriptAnnotationWithUnsupportedFormat
   {
      @ApplyScriptBefore("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
   }

   @ApplyScriptBefore
   private static class ApplyScriptBeforeAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }

   @ApplyScriptBefore
   private static class UsingScriptAnnotatedOnClassLevelOnlyNonExistingFile
   {
      public void shouldFail() {}
   }

   private static class UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation
   {
      @ApplyScriptBefore("non-existing.sql")
      public void shouldFailForNonExistingFile() {}

      @ApplyScriptBefore("tables-in-scripts-folder.sql")
      public void shouldPassForFileStoredInDefaultLocation() {}

   }

}
