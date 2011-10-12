package org.jboss.arquillian.persistence.metadata;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Format;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
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

   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldFetchDataFileNameFromTestLevelAnnotation() throws Exception
   {
      // given
      String expectedDataFile = XML_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      String dataFile = metadataProvider.dataFile();

      // then
      assertThat(dataFile).isEqualTo(expectedDataFile);
   }
   
   @Test
   public void shouldFetchDataFromClassLevelAnnotationWhenNotDefinedForTestMethod() throws Exception
   {
      // given
      String expectedDataFile = XML_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      String dataFile = metadataProvider.dataFile();

      // then
      assertThat(dataFile).isEqualTo(expectedDataFile);
   }
   
   @Test
   public void shouldFetchDataFormatFromMethodLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.XLS;
      TestEvent testEvent = createTestEvent("shouldPassWithDataAndFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      Format dataFormat = metadataProvider.dataFormat();

      // then
      assertThat(dataFormat).isEqualTo(expectedFormat);
   }
   
   @Test
   public void shouldInferDataFormatFromFileNameWhenNotDefinedOnMethodLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      Format dataFormat = metadataProvider.dataFormat();

      // then
      assertThat(dataFormat).isEqualTo(expectedFormat);
   }
   
   @Test
   public void shouldInferDataFormatFromFileNameWhenNotDefinedOnClassLevelAnnotation() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      TestEvent testEvent = createTestEvent("shouldPassWithoutDataDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      Format dataFormat = metadataProvider.dataFormat();

      // then
      assertThat(dataFormat).isEqualTo(expectedFormat);
   }
   
   @Test(expected = UnsupportedDataFormatException.class)
   public void shouldThrowExceptionWhenFormatCannotBeInferedFromFileExtension() throws Exception
   {
      // given
      Format expectedFormat = Format.XML;
      TestEvent testEvent = createTestEvent("shouldFailWithNonSupportedFileExtension");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      Format dataFormat = metadataProvider.dataFormat();

      // then
      // exception should be thrown      
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
      
      @Data(value = EXCEL_DATA_SET_ON_METHOD_LEVEL, format = Format.XLS)
      public void shouldPassWithDataAndFormatDefinedOnMethodLevel() {}
      
      @Data("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
      
   }
   
}
