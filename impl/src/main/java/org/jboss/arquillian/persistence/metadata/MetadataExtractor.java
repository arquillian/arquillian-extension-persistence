package org.jboss.arquillian.persistence.metadata;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

class MetadataExtractor
{

   private final TestClass testClass;

   private final Method testMethod;

   private final Map<AnnotationLevel, DataSource> dataSourceAnnotations = new EnumMap<AnnotationLevel, DataSource>(
         AnnotationLevel.class);

   private final Map<AnnotationLevel, Data> dataAnnotations = new EnumMap<AnnotationLevel, Data>(
         AnnotationLevel.class);

   private final Map<AnnotationLevel, Transactional> transactionalAnnotations = new EnumMap<AnnotationLevel, Transactional>(
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
      dataAnnotations.put(AnnotationLevel.CLASS, dataClassAnnotation);
      
      Data dataMethodAnnotation = testMethod.getAnnotation(Data.class);
      dataAnnotations.put(AnnotationLevel.METHOD, dataMethodAnnotation);
   }

   private void fetchDataSourceMetadata()
   {
      DataSource dsClassAnnotation = testClass.getAnnotation(DataSource.class);
      dataSourceAnnotations.put(AnnotationLevel.CLASS, dsClassAnnotation);
      
      DataSource dsMethodAnnotation = testMethod.getAnnotation(DataSource.class);
      dataSourceAnnotations.put(AnnotationLevel.METHOD, dsMethodAnnotation);
   }
   
   private void fetchTransactionalMetaData()
   {
      Transactional transactionalClassAnnotation = testClass.getAnnotation(Transactional.class);
      transactionalAnnotations.put(AnnotationLevel.CLASS, transactionalClassAnnotation);
      
      Transactional transactionalMethodAnnotation = testMethod.getAnnotation(Transactional.class);
      transactionalAnnotations.put(AnnotationLevel.METHOD, transactionalMethodAnnotation);
      
   }

   public boolean hasDataAnnotationOn(AnnotationLevel level)
   {
      return getDataAnnotationOn(level) != null;
   }

   public Data getDataAnnotationOn(AnnotationLevel level)
   {
      return dataAnnotations.get(level);
   }
   
   public boolean hasTransactionalAnnotationOn(AnnotationLevel level)
   {
      return transactionalAnnotations.get(level) != null;
   }
   
   public Transactional getTransactionalAnnotationOn(AnnotationLevel level)
   {
      return transactionalAnnotations.get(level);
   }

   public boolean hasDataSourceAnnotationOn(AnnotationLevel level)
   {
      return dataSourceAnnotations.get(level) != null;
   }

   public DataSource getDataSourceAnnotationOn(AnnotationLevel level)
   {
      return dataSourceAnnotations.get(level);
   }

}
