package org.jboss.arquillian.persistence.metadata;

import java.lang.reflect.Method;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Format;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.DefaultFileNamingStrategy;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;
import org.jboss.arquillian.persistence.exception.UnsupportedDataFormatException;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class MetadataProvider
{

   private final PersistenceConfiguration configuration;
   
   private final MetadataExtractor metadataExtractor;
   
   private final TestClass testClass;
   
   private final Method testMethod;
   
   public MetadataProvider(TestClass testClass, Method testMethod, PersistenceConfiguration configuration)
   {
      this.metadataExtractor = new MetadataExtractor(testClass, testMethod);
      this.configuration = configuration;
      this.testClass = testClass;
      this.testMethod = testMethod;
   }
   
   public MetadataProvider(TestEvent testEvent, PersistenceConfiguration configuration)
   {
      this(testEvent.getTestClass(), testEvent.getTestMethod(), configuration);
   }
   
   public boolean isPersistenceFeatureEnabled()
   {

      if (!hasDataAnnotation())
      {
         return false;
      }

      if (!hasDataSourceAnnotation() && !configuration.isDefaultDataSourceDefined())
      {
         throw new DataSourceNotDefinedException("Data source not defined!");
      }

      return true;
   }
   
   public boolean hasDataAnnotation()
   {
      return metadataExtractor.hasDataAnnotationOn(AnnotationLevel.CLASS) 
            || metadataExtractor.hasDataAnnotationOn(AnnotationLevel.METHOD);
   }

   public boolean hasDataSourceAnnotation()
   {
      return metadataExtractor.hasDataSourceAnnotationOn(AnnotationLevel.CLASS) 
            || metadataExtractor.hasDataSourceAnnotationOn(AnnotationLevel.METHOD);
   }

   public String getDataSourceName()
   {
      String dataSource = "";
      
      if (configuration.isDefaultDataSourceDefined())
      {
         dataSource = configuration.getDefaultDataSource();
      }
      
      if (hasDataSourceAnnotation())
      {
         dataSource = getDataSourceAnnotation().value();
      }

      if ("".equals(dataSource.trim()))
      {
         throw new DataSourceNotDefinedException("DataSource not defined");
      }
    
      return dataSource;
   }

   public Format getDataFormat()
   {
      Format format = getDataAnnotation().format();
      
      if (Format.NOT_DEFINED.equals(format))
      {
         format = Format.inferFromFile(getDataFileName());
      }
      
      if (Format.UNSUPPORTED.equals(format))
      {
         throw new UnsupportedDataFormatException("File " + getDataFileName() + " is not supported.");
      }
      
      return format;
   }

   /**
    * Resolves path to data set file defined in {@link Data} annotation.
    * In it's not specified following strategy is applied:
    * <ul>
    *   <li>Assumption that files are stored in <code>datasets</code> folder</li>
    *   <li>
    *       If {@link Data} annotation is defined on method level, file name has following format:
    *       <i>[fully qualified class name]#[test method name].[default format]</i> 
    *   </li>
    *   <li>
    *       If {@link Data} annotation is defined on class level, file name has following format:
    *       <i>[fully qualified class name]#.[default format]</i></li>
    * </ul>
    * If not specified otherwise in arquillian.xml, it's assumed that data set is in
    * xml format.
    * 
    * @return path to data set file
    */
   public String getDataFileName()
   {
      Data dataAnnotation = getDataAnnotation();
      String specifiedFileName = dataAnnotation.value();
      if ("".equals(specifiedFileName.trim()))
      {
         specifiedFileName = getDefaultNamingForDataSetFile();
      }
      return specifiedFileName;
   }

   private String getDefaultNamingForDataSetFile()
   {
      Format format = getDataAnnotation().format();
      if (Format.NOT_DEFINED.equals(format))
      {
         format = configuration.getDefaultDataSetFormat();
      }
      
      if (metadataExtractor.hasDataAnnotationOn(AnnotationLevel.METHOD)) 
      {
         return new DefaultFileNamingStrategy(format).createFileName(testClass.getJavaClass(), testMethod);
      }
      
      return new DefaultFileNamingStrategy(format).createFileName(testClass.getJavaClass());
   }

   public Data getDataAnnotation()
   {
      Data usedAnnotation = metadataExtractor.getDataAnnotationOn(AnnotationLevel.CLASS);
      if (metadataExtractor.hasDataAnnotationOn(AnnotationLevel.METHOD))
      {
         usedAnnotation = metadataExtractor.getDataAnnotationOn(AnnotationLevel.METHOD);
      }
      
      return usedAnnotation;
   }
   
   public DataSource getDataSourceAnnotation()
   {
      DataSource usedAnnotation = metadataExtractor.getDataSourceAnnotationOn(AnnotationLevel.CLASS);
      if (metadataExtractor.hasDataSourceAnnotationOn(AnnotationLevel.METHOD))
      {
         usedAnnotation = metadataExtractor.getDataSourceAnnotationOn(AnnotationLevel.METHOD);
      }
      
      return usedAnnotation;
   }

   public boolean isTransactional()
   {
      return metadataExtractor.hasTransactionalAnnotationOn(AnnotationLevel.CLASS)
            || metadataExtractor.hasTransactionalAnnotationOn(AnnotationLevel.METHOD);
   }

   public TransactionMode getTransactionalMode()
   {
      Transactional transactionalAnnotation = metadataExtractor.getTransactionalAnnotationOn(AnnotationLevel.CLASS);
      if (metadataExtractor.hasTransactionalAnnotationOn(AnnotationLevel.METHOD))
      {
         transactionalAnnotation = metadataExtractor.getTransactionalAnnotationOn(AnnotationLevel.METHOD);
      }
      
      return transactionalAnnotation.value();
   }
   
}
