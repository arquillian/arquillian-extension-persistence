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

import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.persistence.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.data.naming.PrefixedScriptFileNamingStrategy;
import org.jboss.arquillian.persistence.exception.InvalidDataSetLocation;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class SqlScriptProviderForScriptsAppliedAfterTestMethodTest
{

   private static final String DEFAULT_FILENAME_FOR_TEST_METHOD = "after-" + ApplyScriptAfterAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.sql";

   private static final String SQL_DATA_SET_ON_CLASS_LEVEL = "scripts/class-level.sql";

   private static final String SQL_DATA_SET_ON_METHOD_LEVEL = "scripts/method-level.sql";


   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldFetchAllDataSetsDefinedForTestClass() throws Exception
   {
      // given
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      Set<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestClass());

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
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

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
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

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
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> files = scriptsProvider.getResourceFileNames(testEvent.getTestMethod());

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotationOnClassLevel() throws Exception
   {
      // given
      String expectedFileName = "after-" + ApplyScriptAfterAnnotatedOnClassLevelOnly.class.getName() + ".sql";
      TestEvent testEvent = new TestEvent(new ApplyScriptAfterAnnotatedOnClassLevelOnly(), ApplyScriptAfterAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> files = scriptsProvider.getResourceFileNames(testEvent.getTestMethod());

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void shouldExtractAllDataSetFiles() throws Exception
   {
      // given
      SqlScriptResourceDescriptor one = new SqlScriptResourceDescriptor("one.sql");
      SqlScriptResourceDescriptor two = new SqlScriptResourceDescriptor("two.sql");
      SqlScriptResourceDescriptor three = new SqlScriptResourceDescriptor("three.sql");
      TestEvent testEvent = new TestEvent(new ApplyScriptAfterAnnotatedClass(), ApplyScriptAfterAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      assertThat(scriptDescriptors).containsExactly(one, two, three);
   }

   @Test(expected = InvalidDataSetLocation.class)
   public void shouldThrowExceptionForNonExistingFileInferedFromClassLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new ApplyScriptAfterAnnotatedOnClassLevelOnlyNonExistingFile(),
            ApplyScriptAfterAnnotatedOnClassLevelOnlyNonExistingFile.class.getMethod("shouldFail"));
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      // exception should be thrown
   }

   @Test(expected = InvalidDataSetLocation.class)
   public void shouldThrowExceptionForNonExistingFileDefinedOnMethodLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new ApplyScriptAfterOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            ApplyScriptAfterOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldFailForNonExistingFile"));
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      // exception should be thrown
   }

   @Test
   public void shouldFindFileInDefaultLocationIfNotSpecifiedExplicitly() throws Exception
   {
      // given
      SqlScriptResourceDescriptor expectedFile = new SqlScriptResourceDescriptor(defaultConfiguration.getDefaultSqlScriptLocation() + "/tables-in-scripts-folder.sql");
      TestEvent testEvent = new TestEvent(new ApplyScriptAfterOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            ApplyScriptAfterOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldPassForFileStoredInDefaultLocation"));
      SqlScriptProvider<ApplyScriptAfter> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> dataSetDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      assertThat(dataSetDescriptors).containsOnly(expectedFile);
   }

   // ----------------------------------------------------------------------------------------

   private SqlScriptProvider<ApplyScriptAfter> createSqlScriptProviderFor(TestEvent testEvent)
   {
      return SqlScriptProvider
            .forAnnotation(ApplyScriptAfter.class)
            .usingConfiguration(defaultConfiguration)
            .extractingMetadataUsing(new MetadataExtractor(testEvent.getTestClass()))
            .namingFollows(new PrefixedScriptFileNamingStrategy("after-", "sql"))
            .build(new ValueExtractor<ApplyScriptAfter>()
            {
               @Override
               public String[] extract(ApplyScriptAfter a)
               {
                  return a.value();
               }
            });
   }

   private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException
   {
      TestEvent testEvent = new TestEvent(new ApplyScriptAfterAnnotatedClass(), ApplyScriptAfterAnnotatedClass.class.getMethod(testMethod));
      return testEvent;
   }

   @ApplyScriptAfter(SQL_DATA_SET_ON_CLASS_LEVEL)
   private static class ApplyScriptAfterAnnotatedClass
   {
      public void shouldPassWithoutDataDefinedOnMethodLevel() {}

      @ApplyScriptAfter(SQL_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataButWithoutFormatDefinedOnMethodLevel () {}

      @ApplyScriptAfter
      public void shouldPassWithDataFileNotSpecified() {}

      @ApplyScriptAfter({"one.sql", "two.sql", "three.sql"})
      public void shouldPassWithMultipleFilesDefined() {}

   }

   private static class UsingScriptAnnotationWithUnsupportedFormat
   {
      @ApplyScriptAfter("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
   }

   @ApplyScriptAfter
   private static class ApplyScriptAfterAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }

   @ApplyScriptAfter
   private static class ApplyScriptAfterAnnotatedOnClassLevelOnlyNonExistingFile
   {
      public void shouldFail() {}
   }

   private static class ApplyScriptAfterOnTestMethodLevelWithNonExistingFileAndDefaultLocation
   {
      @ApplyScriptAfter("non-existing.sql")
      public void shouldFailForNonExistingFile() {}

      @ApplyScriptAfter("tables-in-scripts-folder.sql")
      public void shouldPassForFileStoredInDefaultLocation() {}

   }

}
