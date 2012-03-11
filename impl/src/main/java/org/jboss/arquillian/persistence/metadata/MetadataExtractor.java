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
import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.spi.TestClass;

/**
*
* @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
*
*/
public class MetadataExtractor
{

   private final TestClass testClass;

   private final Map<Class<?>, AnnotationInspector<?>> inspectors = new HashMap<Class<?>, AnnotationInspector<?>>();

   public MetadataExtractor(TestClass testClass)
   {
      this.testClass = testClass;
      register(testClass, DataSource.class);
      register(testClass, UsingDataSet.class);
      register(testClass, ShouldMatchDataSet.class);
      register(testClass, ApplyScriptBefore.class);
      register(testClass, ApplyScriptAfter.class);
      register(testClass, Transactional.class);
      register(testClass, Cleanup.class);
   }

   public <K extends Annotation> void register(final TestClass testClass, final Class<K> annotation)
   {
      inspectors.put(annotation, new AnnotationInspector<K>(testClass, annotation));
   }

   @SuppressWarnings("unchecked")
   public <K extends Annotation> AnnotationInspector<K> using(final Class<K> annotation)
   {
      return (AnnotationInspector<K>) inspectors.get(annotation);
   }

   public AnnotationInspector<DataSource> dataSource()
   {
      return using(DataSource.class);
   }

   public AnnotationInspector<UsingDataSet> usingDataSet()
   {
      return using(UsingDataSet.class);
   }

   public AnnotationInspector<ShouldMatchDataSet> shouldMatchDataSet()
   {
      return using(ShouldMatchDataSet.class);
   }

   public AnnotationInspector<ApplyScriptBefore> applyScriptBefore()
   {
      return using(ApplyScriptBefore.class);
   }

   public AnnotationInspector<ApplyScriptAfter> applyScriptAfter()
   {
      return using(ApplyScriptAfter.class);
   }

   public AnnotationInspector<Transactional> transactional()
   {
      return using(Transactional.class);
   }

   public AnnotationInspector<Cleanup> cleanup()
   {
      return using(Cleanup.class);
   }

   public boolean hasPersistenceTestAnnotation()
   {
      return testClass.getAnnotation(PersistenceTest.class) != null;
   }

   public Class<?> getJavaClass()
   {
      return testClass.getJavaClass();
   }

}
