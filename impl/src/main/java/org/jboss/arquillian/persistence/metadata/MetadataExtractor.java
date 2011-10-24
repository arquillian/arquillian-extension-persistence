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
import java.util.EnumMap;
import java.util.Map;

import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

class MetadataExtractor
{

   private final TestClass testClass;

   private final Method testMethod;

   private final Map<AnnotationLevel, DataSource> dataSourceAnnotations = new EnumMap<AnnotationLevel, DataSource>(
         AnnotationLevel.class);

   private final Map<AnnotationLevel, Data> dataAnnotations = new EnumMap<AnnotationLevel, Data>(AnnotationLevel.class);

   private final Map<AnnotationLevel, Expected> expectedAnnotations = new EnumMap<AnnotationLevel, Expected>(
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
      fetch(Data.class, dataAnnotations);
      fetch(Expected.class, expectedAnnotations);
      fetch(DataSource.class, dataSourceAnnotations);
      fetch(Transactional.class, transactionalAnnotations);
   }

   private <T extends Annotation> void fetch(Class<T> annotation, Map<AnnotationLevel, T> map)
   {
      T classAnnotation = testClass.getAnnotation(annotation);
      map.put(AnnotationLevel.CLASS, classAnnotation);

      T methodAnnotation = testMethod.getAnnotation(annotation);
      map.put(AnnotationLevel.METHOD, methodAnnotation);
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
   
   public boolean hasTransactionalSupportEnabledOn(AnnotationLevel level)
   {
      boolean isTransactionalSupportEnabled = true;
      if (hasTransactionalAnnotationOn(level))
      {
         Transactional transactional = getTransactionalAnnotationOn(level);
         isTransactionalSupportEnabled = !TransactionMode.DISABLED.equals(transactional.value());
      }
      return isTransactionalSupportEnabled;
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

   public boolean hasExpectedAnnotationOn(AnnotationLevel level)
   {
      return expectedAnnotations.get(level) != null;
   }

   public Expected getExpectedAnnotationOn(AnnotationLevel level)
   {
      return expectedAnnotations.get(level);
   }

   public boolean hasPersistenceTestAnnotation()
   {
      return testClass.getAnnotation(PersistenceTest.class) != null;
   }

}
