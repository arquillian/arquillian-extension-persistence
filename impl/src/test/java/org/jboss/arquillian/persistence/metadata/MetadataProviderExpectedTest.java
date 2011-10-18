package org.jboss.arquillian.persistence.metadata;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.ConfigurationLoader;
import org.jboss.arquillian.persistence.data.DataSetDescriptor;
import org.jboss.arquillian.persistence.data.Format;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;
import org.jboss.arquillian.persistence.exception.UnsupportedDataFormatException;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

@SuppressWarnings("unused")
public class MetadataProviderExpectedTest
{

   private static final String XML_EXPECTED_DATA_SET_ON_CLASS_LEVEL = "datasets/xml/expected-class-level.xml";

   private static final String XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL = "datasets/xml/expected-method-level.xml";
   
   private static final String EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL = "datasets/xls/expected-method-level.xls";

   private PersistenceConfiguration defaultConfiguration = ConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldFetchDataFileNameFromTestLevelAnnotation() throws Exception
   {
      // given
      String expectedDataFile = XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<String> dataFiles = metadataProvider.getExpectedDataFileNames();

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }
   
   @Test
   public void shouldFetchDataFromClassLevelAnnotationWhenNotDefinedForTestMethod() throws Exception
   {
      // given
      String expectedDataFile = XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<String> dataFiles = metadataProvider.getExpectedDataFileNames();

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }
   
   @Test
   public void shouldFetchDataFormatFromMethodLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.EXCEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataAndFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<Format> dataFormats = metadataProvider.getExpectedDataFormats();

      // then
      assertThat(dataFormats).containsOnly(expectedFormat);
   }
   
   @Test
   public void shouldInferDataFormatFromFileNameWhenNotDefinedOnMethodLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<Format> dataFormats = metadataProvider.getExpectedDataFormats();

      // then
      assertThat(dataFormats).containsOnly(expectedFormat);
   }
   
   @Test
   public void shouldInferDataFormatFromFileNameWhenNotDefinedOnClassLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      TestEvent testEvent = createTestEvent("shouldPassWithoutDataDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<Format> dataFormats = metadataProvider.getExpectedDataFormats();

      // then
      assertThat(dataFormats).containsOnly(expectedFormat);
   }
   
   @Test(expected = UnsupportedDataFormatException.class)
   public void shouldThrowExceptionWhenFormatCannotBeInferedFromFileExtension() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      TestEvent testEvent = createTestEvent("shouldFailWithNonSupportedFileExtension");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<Format> expectedDataFormats = metadataProvider.getExpectedDataFormats();

      // then
      // exception should be thrown      
   }
   
   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotation() throws Exception
   {
      // given
      String expectedFileName = "expected-" + ExpectedDataAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.xls";
      TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<String> files = metadataProvider.getExpectedDataFileNames();

      // then
      assertThat(files).containsOnly(expectedFileName);
   }
   
   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotationOnClassLevel() throws Exception
   {
      // given
      String expectedFileName = "expected-" + ExpectedDataAnnotatedOnClassLevelOnly.class.getName() + ".xls";
      TestEvent testEvent = new TestEvent(new ExpectedDataAnnotatedOnClassLevelOnly(), ExpectedDataAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<String> files = metadataProvider.getExpectedDataFileNames();

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
      TestEvent testEvent = new TestEvent(new ExpectedDataAnnotatedClass(), ExpectedDataAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<DataSetDescriptor> dataSetDescriptors = metadataProvider.getExpectedtDataSetDescriptors();
      
      // then
      assertThat(dataSetDescriptors).containsExactly(xml, xls, yml);
   }
   
   
   
   // ---------------------------------------------------------------------------------------- 
   
   private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException
   {
      TestEvent testEvent = new TestEvent(new ExpectedDataAnnotatedClass(), ExpectedDataAnnotatedClass.class.getMethod(testMethod));
      return testEvent;
   }
   
   @Data("datasets/test.xml")
   @Expected(XML_EXPECTED_DATA_SET_ON_CLASS_LEVEL)
   private static class ExpectedDataAnnotatedClass
   {
      public void shouldPassWithoutDataDefinedOnMethodLevel() {}

      @Expected(XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataButWithoutFormatDefinedOnMethodLevel () {}
      
      @Expected(value = EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataAndFormatDefinedOnMethodLevel() {}
      
      @Expected("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
      
      @Expected
      public void shouldPassWithDataFileNotSpecified() {}
      
      @Expected({"one.xml", "two.xls", "three.yml"})
      public void shouldPassWithMultipleFilesDefined() {}
   }
   
   @Data
   @Expected
   private static class ExpectedDataAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }
   
}
