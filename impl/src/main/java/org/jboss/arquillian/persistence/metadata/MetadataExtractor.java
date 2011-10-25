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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.test.spi.TestClass;

public class MetadataExtractor
{

   private final TestClass testClass;

   private Map<Method,DataSource> dataSourceAnnotations;

   private Map<Method,Data> dataAnnotations;
   
   private Map<Method,Expected> expectedAnnotations;

   private Map<Method,Transactional> transactionalAnnotations;

   public MetadataExtractor(TestClass testClass)
   {
      this.testClass = testClass;
      prefetchPersistenceAnnotations();
   }

   public boolean hasDataAnnotationOn(Method testMethod)
   {
      return getDataAnnotationOn(testMethod) != null;
   }

   public Data getDataAnnotationOn(Method testMethod)
   {
      return dataAnnotations.get(testMethod);
   }

   public boolean hasTransactionalAnnotationOn(Method testMethod)
   {
      return transactionalAnnotations.get(testMethod) != null;
   }
   
   public Transactional getTransactionalAnnotationOn(Method testMethod)
   {
      return transactionalAnnotations.get(testMethod);
   }

   public boolean hasDataSourceAnnotationOn(Method testMethod)
   {
      return dataSourceAnnotations.get(testMethod) != null;
   }

   public DataSource getDataSourceAnnotationOn(Method testMethod)
   {
      return dataSourceAnnotations.get(testMethod);
   }

   public boolean hasExpectedAnnotationOn(Method testMethod)
   {
      return expectedAnnotations.get(testMethod) != null;
   }

   public Expected getExpectedAnnotationOn(Method testMethod)
   {
      return expectedAnnotations.get(testMethod);
   }

   public boolean hasPersistenceTestAnnotation()
   {
      return testClass.getAnnotation(PersistenceTest.class) != null;
   }
   
   public Transactional getTransactionalAnnotationOnClassLevel()
   {
      return getAnnotationOnClassLevel(Transactional.class);
   }

   public Expected getExpectedAnnotationOnClassLevel()
   {
      return getAnnotationOnClassLevel(Expected.class);
   }
   
   public boolean hasExpectedAnnotationOnClassLevel()
   {
      return getExpectedAnnotationOnClassLevel() != null;
   }

   public Data getDataAnnotationOnClassLevel()
   {
      return getAnnotationOnClassLevel(Data.class);
   }

   public boolean hasDataAnnotationOnClassLevel()
   {
      return getDataAnnotationOnClassLevel() != null;
   }

   public DataSource getDataSourceAnnotationOnClassLevel()
   {
      return getAnnotationOnClassLevel(DataSource.class);
   }

   public boolean hasDataSourceAnnotationOnClassLevel()
   {
      return getDataSourceAnnotationOnClassLevel() != null;
   }

   // Private
   
   private void prefetchPersistenceAnnotations()
   {
      dataAnnotations = fetch(Data.class);
      expectedAnnotations = fetch(Expected.class);
      dataSourceAnnotations = fetch(DataSource.class);
      transactionalAnnotations = fetch(Transactional.class);
   }

   private <T extends Annotation> Map<Method, T> fetch(Class<T> annotation)
   {
      final Map<Method, T> map = new HashMap<Method, T>();
      
      for (Method testMethod : testClass.getMethods(annotation))
      {
         map.put(testMethod, testMethod.getAnnotation(annotation));
      }
      
      return map;
   }

   public Class<?> getJavaClass()
   {
      return testClass.getJavaClass();
   }
   
   private <T extends Annotation> T getAnnotationOnClassLevel(Class<T> annotation)
   {
      return testClass.getAnnotation(annotation);
   }
}
