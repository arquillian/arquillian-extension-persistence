package org.jboss.arquillian.persistence.metadata;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Format;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

class MetadataExtractor
{

   private static enum AnnotationLevel {
      CLASS, METHOD;
   }

   private final TestClass testClass;

   private final Method testMethod;

   private final Map<AnnotationLevel, DataSource> dataSourceAnnotations = new EnumMap<MetadataExtractor.AnnotationLevel, DataSource>(
         AnnotationLevel.class);

   private final Map<AnnotationLevel, Data> dataAnnotations = new EnumMap<MetadataExtractor.AnnotationLevel, Data>(
         AnnotationLevel.class);

   private final Map<AnnotationLevel, Transactional> transactionalAnnotations = new EnumMap<MetadataExtractor.AnnotationLevel, Transactional>(
         AnnotationLevel.class);
   
   public MetadataExtractor(TestClass testClass, Method testMethod)
   {
      this.testClass = testClass;
      this.testMethod = testMethod;
      prefetch();
   }

   public MetadataExtractor(TestEvent testEvent)
   {
      this(testEvent.getTestClass(), testEvent.getTestMethod());
   }

   private void prefetch()
   {
      fetchDataMetadata();
      fetchDataSourceMetadata();
      fetchTransactionalMetaData();
   }

   private void fetchDataMetadata()
   {
      Data dataClassAnnotation = testClass.getAnnotation(Data.class);
      if (dataClassAnnotation == null)
      {
         dataClassAnnotation = Data.NOT_DEFINED;
      }
      dataAnnotations.put(AnnotationLevel.CLASS, dataClassAnnotation);
      
      Data dataMethodAnnotation = testMethod.getAnnotation(Data.class);
      if (dataMethodAnnotation == null)
      {
         dataMethodAnnotation = Data.NOT_DEFINED;
      }
      dataAnnotations.put(AnnotationLevel.METHOD, dataMethodAnnotation);
   }

   private void fetchDataSourceMetadata()
   {
      DataSource dsClassAnnotation = testClass.getAnnotation(DataSource.class);
      if (dsClassAnnotation == null)
      {
         dsClassAnnotation = DataSource.NOT_DEFINED;
      }
      dataSourceAnnotations.put(AnnotationLevel.CLASS, dsClassAnnotation);
      
      DataSource dsMethodAnnotation = testMethod.getAnnotation(DataSource.class);
      if (dsMethodAnnotation == null)
      {
         dsMethodAnnotation = DataSource.NOT_DEFINED;
      }
      dataSourceAnnotations.put(AnnotationLevel.METHOD, dsMethodAnnotation);
   }
   
   private void fetchTransactionalMetaData()
   {
      Transactional transactionalClassAnnotation = testClass.getAnnotation(Transactional.class);
      if (transactionalClassAnnotation == null)
      {
         transactionalClassAnnotation = Transactional.NOT_DEFINED;
      }
      transactionalAnnotations.put(AnnotationLevel.CLASS, transactionalClassAnnotation);
      
      Transactional transactionalMethodAnnotation = testMethod.getAnnotation(Transactional.class);
      if (transactionalMethodAnnotation == null)
      {
         transactionalMethodAnnotation = Transactional.NOT_DEFINED;
      }
      transactionalAnnotations.put(AnnotationLevel.METHOD, transactionalMethodAnnotation);
      
   }

   public boolean hasDataAnnotation()
   {
      final Data classLevelAnnotation = dataAnnotations.get(AnnotationLevel.CLASS);
      final Data methodLevelAnnotation = dataAnnotations.get(AnnotationLevel.METHOD);
      return !Data.NOT_DEFINED.equals(classLevelAnnotation) || !Data.NOT_DEFINED.equals(methodLevelAnnotation);
   }

   public boolean hasDataSourceAnnotation()
   {
      DataSource classLevelAnnotation = dataSourceAnnotations.get(AnnotationLevel.CLASS);
      DataSource methodLevelAnnotation = dataSourceAnnotations.get(AnnotationLevel.METHOD);
      return !DataSource.NOT_DEFINED.equals(classLevelAnnotation) || !DataSource.NOT_DEFINED.equals(methodLevelAnnotation);
   }

   public DataSource getDataSourceAnnotation()
   {
      DataSource usedAnnotation = dataSourceAnnotations.get(AnnotationLevel.METHOD);
      if (usedAnnotation.equals(DataSource.NOT_DEFINED))
      {
         usedAnnotation = dataSourceAnnotations.get(AnnotationLevel.CLASS);
      }
      
      return usedAnnotation;
   }
   
   public Data getDataAnnotation()
   {
      Data usedAnnotation = dataAnnotations.get(AnnotationLevel.METHOD);
      if (usedAnnotation.equals(Data.NOT_DEFINED))
      {
         usedAnnotation = dataAnnotations.get(AnnotationLevel.CLASS);
      }
      
      return usedAnnotation;
   }
   
   public boolean isTransactional()
   {
      Transactional classLevelAnnotation = transactionalAnnotations.get(AnnotationLevel.CLASS);
      Transactional methodLevelAnnotation = transactionalAnnotations.get(AnnotationLevel.METHOD);
      return !Transactional.NOT_DEFINED.equals(classLevelAnnotation) || !Transactional.NOT_DEFINED.equals(methodLevelAnnotation);
   }
   
   public Format dataFormat()
   {
      return getDataAnnotation().format();
   }

   public String dataSetFile()
   {
      return getDataAnnotation().value();
   }

   public Transactional getTransactionalAnnotation()
   {
      Transactional usedAnnotation = transactionalAnnotations.get(AnnotationLevel.METHOD);
      if (usedAnnotation.equals(Transactional.NOT_DEFINED))
      {
         usedAnnotation = transactionalAnnotations.get(AnnotationLevel.CLASS);
      }
      
      return usedAnnotation;
   }

}
