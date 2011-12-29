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

import org.jboss.arquillian.persistence.UsingScript;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.persistence.data.descriptor.SqlScriptDescriptor;
import org.jboss.arquillian.persistence.exception.InvalidDataSetLocation;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class SqlScriptProviderTest
{

   private static final String DEFAULT_FILENAME_FOR_TEST_METHOD = UsingScriptAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.sql";

   private static final String SQL_DATA_SET_ON_CLASS_LEVEL = "scripts/class-level.sql";

   private static final String SQL_DATA_SET_ON_METHOD_LEVEL = "scripts/method-level.sql";


   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldFetchAllDataSetsDefinedForTestClass() throws Exception
   {
      // given
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      SqlScriptProvider scriptProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      Set<SqlScriptDescriptor> scriptDescriptors = scriptProvider.getDescriptors(testEvent.getTestClass());

      // then
      SqlScriptDescriptorAssert.assertThat(scriptDescriptors).containsOnlyFollowingFiles(SQL_DATA_SET_ON_CLASS_LEVEL,
            SQL_DATA_SET_ON_METHOD_LEVEL, DEFAULT_FILENAME_FOR_TEST_METHOD, "one.sql", "two.sql", "three.sql");

   }

   @Test
   public void shouldFetchDataFileNameFromTestLevelAnnotation() throws Exception
   {
      // given
      String expectedDataFile = SQL_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      SqlScriptProvider scriptsProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> dataFiles = scriptsProvider.getResourceFileNames(testEvent.getTestMethod());

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }

   @Test
   public void shouldFetchDataFromClassLevelAnnotationWhenNotDefinedForTestMethod() throws Exception
   {
      // given
      String expectedDataFile = SQL_DATA_SET_ON_CLASS_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithoutDataDefinedOnMethodLevel");
      SqlScriptProvider scriptsProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> dataFiles = scriptsProvider.getResourceFileNames(testEvent.getTestMethod());

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }

   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotation() throws Exception
   {
      // given
      String expectedFileName = DEFAULT_FILENAME_FOR_TEST_METHOD;
      TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
      SqlScriptProvider scriptsProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> files = scriptsProvider.getResourceFileNames(testEvent.getTestMethod());

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotationOnClassLevel() throws Exception
   {
      // given
      String expectedFileName = UsingScriptAnnotatedOnClassLevelOnly.class.getName() + ".sql";
      TestEvent testEvent = new TestEvent(new UsingScriptAnnotatedOnClassLevelOnly(), UsingScriptAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      SqlScriptProvider scriptsProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<String> files = scriptsProvider.getResourceFileNames(testEvent.getTestMethod());

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void shouldExtractAllDataSetFiles() throws Exception
   {
      // given
      SqlScriptDescriptor one = new SqlScriptDescriptor("one.sql");
      SqlScriptDescriptor two = new SqlScriptDescriptor("two.sql");
      SqlScriptDescriptor three = new SqlScriptDescriptor("three.sql");
      TestEvent testEvent = new TestEvent(new UsingScriptAnnotatedClass(), UsingScriptAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
      SqlScriptProvider scriptsProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<SqlScriptDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      assertThat(scriptDescriptors).containsExactly(one, two, three);
   }

   @Test(expected = InvalidDataSetLocation.class)
   public void shouldThrowExceptionForNonExistingFileInferedFromClassLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new UsingScriptAnnotatedOnClassLevelOnlyNonExistingFile(),
            UsingScriptAnnotatedOnClassLevelOnlyNonExistingFile.class.getMethod("shouldFail"));
      SqlScriptProvider scriptsProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<SqlScriptDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      // exception should be thrown
   }

   @Test(expected = InvalidDataSetLocation.class)
   public void shouldThrowExceptionForNonExistingFileDefinedOnMethodLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldFailForNonExistingFile"));
      SqlScriptProvider scriptsProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<SqlScriptDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      // exception should be thrown
   }

   @Test
   public void shouldFindFileInDefaultLocationIfNotSpecifiedExplicitly() throws Exception
   {
      // given
      SqlScriptDescriptor expectedFile = new SqlScriptDescriptor(defaultConfiguration.getDefaultSqlScriptLocation() + "/tables-in-scripts-folder.sql");
      TestEvent testEvent = new TestEvent(new UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldPassForFileStoredInDefaultLocation"));
      SqlScriptProvider scriptsProvider = new SqlScriptProvider(new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

      // when
      List<SqlScriptDescriptor> dataSetDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      assertThat(dataSetDescriptors).containsOnly(expectedFile);
   }

   // ----------------------------------------------------------------------------------------

   private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException
   {
      TestEvent testEvent = new TestEvent(new UsingScriptAnnotatedClass(), UsingScriptAnnotatedClass.class.getMethod(testMethod));
      return testEvent;
   }

   @UsingScript(SQL_DATA_SET_ON_CLASS_LEVEL)
   private static class UsingScriptAnnotatedClass
   {
      public void shouldPassWithoutDataDefinedOnMethodLevel() {}

      @UsingScript(SQL_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataButWithoutFormatDefinedOnMethodLevel () {}

      @UsingScript
      public void shouldPassWithDataFileNotSpecified() {}

      @UsingScript({"one.sql", "two.sql", "three.sql"})
      public void shouldPassWithMultipleFilesDefined() {}

   }

   private static class UsingScriptAnnotationWithUnsupportedFormat
   {
      @UsingScript("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
   }

   @UsingScript
   private static class UsingScriptAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }

   @UsingScript
   private static class UsingScriptAnnotatedOnClassLevelOnlyNonExistingFile
   {
      public void shouldFail() {}
   }

   private static class UsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation
   {
      @UsingScript("non-existing.sql")
      public void shouldFailForNonExistingFile() {}

      @UsingScript("tables-in-scripts-folder.sql")
      public void shouldPassForFileStoredInDefaultLocation() {}

   }

}
