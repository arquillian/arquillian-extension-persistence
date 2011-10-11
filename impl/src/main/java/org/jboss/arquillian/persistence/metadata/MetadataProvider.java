package org.jboss.arquillian.persistence.metadata;

import java.lang.reflect.Method;

import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.SourceType;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.exception.DataSourceNotDefinedException;
import org.jboss.arquillian.persistence.exception.UnsupportedDataSetTypeException;
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
   
   public boolean isPersistenceInteractionApplicable()
   {

      if (!metadataExtractor.hasDataAnnotation())
      {
         return false;
      }

      if (!metadataExtractor.hasDataSourceAnnotation() && !configuration.isDefaultDataSourceDefined())
      {
         throw new RuntimeException("Data source not defined!");
      }

      return true;
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

   public SourceType dataSetType()
   {
      SourceType type = metadataExtractor.dataSetType();
      if (SourceType.NOT_DEFINED.equals(type))
      {
         type = SourceType.inferFromFile(metadataExtractor.dataSetFile());
      }
      
      if (SourceType.UNSUPPORTED.equals(type))
      {
         // TODO more meaningful 
         throw new UnsupportedDataSetTypeException("File " + dataSetFile() + " is not supported.");
      }
      
      return type;
   }

   public String dataSetFile()
   {
      // TODO if not there follow convention datasource/full-class-name.method-name.(type)
      return metadataExtractor.getDataAnnotation().value();
   }

   public boolean isTransactional()
   {
      return metadataExtractor.isTransactional();
   }

   public TransactionMode getTransactionalMode()
   {
      Transactional transactionalAnnotation = metadataExtractor.getTransactionalAnnotation();
      return transactionalAnnotation.value();
   }
   
}
