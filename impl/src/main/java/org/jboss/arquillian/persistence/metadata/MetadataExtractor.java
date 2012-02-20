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

import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.persistence.UsingScript;
import org.jboss.arquillian.test.spi.TestClass;

/**
*
* @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
*
*/
public class MetadataExtractor
{

   private final TestClass testClass;

   private final AnnotationInspector<DataSource> dataSourceInspector;

   private final AnnotationInspector<UsingDataSet> usingDataSetInspector;

   private final AnnotationInspector<ShouldMatchDataSet> shouldMatchDataSetInspector;

   private final AnnotationInspector<UsingScript> usingScriptInspector;

   private final AnnotationInspector<Transactional> transactionalInspector;

   public MetadataExtractor(TestClass testClass)
   {
      this.testClass = testClass;
      this.dataSourceInspector = new AnnotationInspector<DataSource>(testClass, DataSource.class);
      this.usingDataSetInspector = new AnnotationInspector<UsingDataSet>(testClass, UsingDataSet.class);
      this.shouldMatchDataSetInspector = new AnnotationInspector<ShouldMatchDataSet>(testClass, ShouldMatchDataSet.class);
      this.usingScriptInspector = new AnnotationInspector<UsingScript>(testClass, UsingScript.class);
      this.transactionalInspector = new AnnotationInspector<Transactional>(testClass, Transactional.class);
   }

   public AnnotationInspector<DataSource> dataSource()
   {
      return dataSourceInspector;
   }

   public AnnotationInspector<UsingDataSet> usingDataSet()
   {
      return usingDataSetInspector;
   }

   public AnnotationInspector<ShouldMatchDataSet> shouldMatchDataSet()
   {
      return shouldMatchDataSetInspector;
   }

   public AnnotationInspector<UsingScript> usingScript()
   {
      return usingScriptInspector;
   }

   public AnnotationInspector<Transactional> transactional()
   {
      return transactionalInspector;
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
