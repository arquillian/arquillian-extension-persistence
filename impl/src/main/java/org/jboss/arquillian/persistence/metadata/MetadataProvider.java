package org.jboss.arquillian.persistence.metadata;

import java.lang.reflect.Method;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Format;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;
import org.jboss.arquillian.persistence.exception.UnsupportedDataFormatException;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class MetadataProvider
{

   private final PersistenceConfiguration configuration;
   
   private final MetadataExtractor metadataExtractor;
   
   public MetadataProvider(TestClass testClass, Method testMethod, PersistenceConfiguration configuration)
   {
      this.metadataExtractor = new MetadataExtractor(testClass, testMethod);
      this.configuration = configuration;
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
      
      DataSource dataSourceAnnotation = metadataExtractor.getDataSourceAnnotation();
      if (!DataSource.NOT_DEFINED.equals(dataSourceAnnotation))
      {
         dataSource = dataSourceAnnotation.value();
      }

      if ("".equals(dataSource))
      {
         throw new DataSourceNotDefinedException("DataSource not defined");
      }
    
      return dataSource;
   }

   public Format dataFormat()
   {
      Format format = getDataAnnotation().format();
      
      if (Format.NOT_DEFINED.equals(format))
      {
         format = Format.inferFromFile(dataFile());
      }
      
      if (Format.UNSUPPORTED.equals(format))
      {
         throw new UnsupportedDataFormatException("File " + dataFile() + " is not supported.");
      }
      
      return format;
   }

   public String dataFile()
   {
      // TODO if not there follow convention datasets/full-class-name.method-name.(type)
      Data dataAnnotation = getDataAnnotation();
      return dataAnnotation.value();
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
