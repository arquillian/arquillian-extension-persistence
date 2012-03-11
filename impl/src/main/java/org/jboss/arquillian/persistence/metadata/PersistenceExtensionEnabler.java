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

import org.jboss.arquillian.test.spi.TestClass;

/**
*
* @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
*
*/
public class PersistenceExtensionEnabler
{

   private final MetadataExtractor metadataExtractor;

   public PersistenceExtensionEnabler(TestClass testClass)
   {
      this.metadataExtractor =  new MetadataExtractor(testClass);
   }

   public PersistenceExtensionEnabler(MetadataExtractor metadataExtractor)
   {
      this.metadataExtractor = metadataExtractor;
   }

   public boolean isPersistenceExtensionRequired()
   {
      return (hasDataSetAnnotation() || hasApplyScriptAnnotation()
            || hasPersistenceTestAnnotation() || hasTransactionalAnnotation());
   }

   // ---------------------------------------------------------------------------------------------------
   // Internal methods
   // ---------------------------------------------------------------------------------------------------

   private boolean hasDataSetAnnotation()
   {
      return metadataExtractor.usingDataSet().isDefinedOnClassLevel()
            || metadataExtractor.usingDataSet().isDefinedOnAnyMethod()
            || metadataExtractor.shouldMatchDataSet().isDefinedOnClassLevel()
            || metadataExtractor.shouldMatchDataSet().isDefinedOnAnyMethod();
   }

   private boolean hasApplyScriptAnnotation()
   {
      return metadataExtractor.applyScriptBefore().isDefinedOnClassLevel()
            || metadataExtractor.applyScriptBefore().isDefinedOnAnyMethod()
            || metadataExtractor.applyScriptAfter().isDefinedOnClassLevel()
            || metadataExtractor.applyScriptAfter().isDefinedOnAnyMethod();
   }

   private boolean hasPersistenceTestAnnotation()
   {
      return metadataExtractor.hasPersistenceTestAnnotation();
   }

   private boolean hasTransactionalAnnotation()
   {
      return metadataExtractor.transactional().isDefinedOnClassLevel()
            || metadataExtractor.transactional().isDefinedOnAnyMethod();
   }

}
