package org.jboss.arquillian.persistence.metadata;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.Format;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.ConfigurationLoader;
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
      String dataFile = metadataProvider.getExpectedDataFileName();

      // then
      assertThat(dataFile).isEqualTo(expectedDataFile);
   }
   
   @Test
   public void shouldFetchDataFromClassLevelAnnotationWhenNotDefinedForTestMethod() throws Exception
   {
      // given
      String expectedDataFile = XML_EXPECTED_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      String dataFile = metadataProvider.getExpectedDataFileName();

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
      Format dataFormat = metadataProvider.getExpectedDataFormat();

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
      Format dataFormat = metadataProvider.getExpectedDataFormat();

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
      Format dataFormat = metadataProvider.getExpectedDataFormat();

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
      Format dataFormat = metadataProvider.getExpectedDataFormat();

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
      String file = metadataProvider.getExpectedDataFileName();

      // then
      assertThat(file).isEqualTo(expectedFileName);
   }
   
   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotationOnClassLevel() throws Exception
   {
      // given
      String expectedFileName = "expected-" + ExpectedDataAnnotatedOnClassLevelOnly.class.getName() + ".xls";
      TestEvent testEvent = new TestEvent(new ExpectedDataAnnotatedOnClassLevelOnly(), ExpectedDataAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      MetadataProvider metadataProvider = new MetadataProvider(testEvent, defaultConfiguration);

      // when
      String file = metadataProvider.getExpectedDataFileName();

      // then
      assertThat(file).isEqualTo(expectedFileName);
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
      
      @Expected(value = EXCEL_EXPECTED_DATA_SET_ON_METHOD_LEVEL, format = Format.XLS)
      public void shouldPassWithDataAndFormatDefinedOnMethodLevel() {}
      
      @Expected("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
      
      @Expected
      public void shouldPassWithDataFileNotSpecified() {}
   }
   
   @Data
   @Expected
   private static class ExpectedDataAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }
   
}
