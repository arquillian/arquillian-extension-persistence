package org.jboss.arquillian.persistence.metadata;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.ConfigurationLoader;
import org.jboss.arquillian.persistence.data.DataSetDescriptor;
import org.jboss.arquillian.persistence.data.Format;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;
import org.jboss.arquillian.persistence.exception.UnsupportedDataFormatException;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

@SuppressWarnings("unused")
public class MetadataProviderDataTest
{

   private static final String XML_DATA_SET_ON_CLASS_LEVEL = "datasets/xml/class-level.xml";

   private static final String XML_DATA_SET_ON_METHOD_LEVEL = "datasets/xml/method-level.xml";
   
   private static final String EXCEL_DATA_SET_ON_METHOD_LEVEL = "datasets/xls/method-level.xls";

   private PersistenceConfiguration defaultConfiguration = ConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldFetchDataFileNameFromTestLevelAnnotation() throws Exception
   {
      // given
      String expectedDataFile = XML_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<String> dataFiles = metadataProvider.getDataFileNames();

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }
   
   @Test
   public void shouldFetchDataFromClassLevelAnnotationWhenNotDefinedForTestMethod() throws Exception
   {
      // given
      String expectedDataFile = XML_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<String> dataFiles = metadataProvider.getDataFileNames();

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
      List<Format> dataFormats = metadataProvider.getDataFormats();

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
      List<Format> dataFormats = metadataProvider.getDataFormats();

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
      List<Format> dataFormats = metadataProvider.getDataFormats();

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
      List<Format> dataFormats = metadataProvider.getDataFormats();

      // then
      // exception should be thrown      
   }
   
   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotation() throws Exception
   {
      // given
      String expectedFileName = DataAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.xls";
      TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<String> files = metadataProvider.getDataFileNames();

      // then
      assertThat(files).containsOnly(expectedFileName);
   }
   
   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotationOnClassLevel() throws Exception
   {
      // given
      String expectedFileName = DataAnnotatedOnClassLevelOnly.class.getName() + ".xls";
      TestEvent testEvent = new TestEvent(new DataAnnotatedOnClassLevelOnly(), DataAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<String> files = metadataProvider.getDataFileNames();

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
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      List<DataSetDescriptor> dataSetDescriptors = metadataProvider.getDataSetDescriptors();
      
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
      
      @Data("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
      
      @Data
      public void shouldPassWithDataFileNotSpecified() {}
      
      @Data({"one.xml", "two.xls", "three.yml"})
      public void shouldPassWithMultipleFilesDefined() {}
   }
   
   @Data
   private static class DataAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }
   
}
