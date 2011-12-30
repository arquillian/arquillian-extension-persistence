/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.persistence.UsingScript;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.descriptor.SqlScriptDescriptor;
import org.jboss.arquillian.persistence.data.naming.CustomScriptFileNamingStrategy;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class SqlScriptProvider extends ResourceProvider<SqlScriptDescriptor>
{

   public SqlScriptProvider(MetadataExtractor metadataExtractor, PersistenceConfiguration configuration)
   {
      super(UsingScript.class, configuration, metadataExtractor);
   }

   @Override
   protected SqlScriptDescriptor createDescriptor(String dataFileName)
   {
      return new SqlScriptDescriptor(determineLocation(dataFileName));
   }

   @Override
   protected String defaultLocation()
   {
      return configuration.getDefaultSqlScriptLocation();
   }

   @Override
   protected String defaultFileName()
   {
      String defaultFileName = new CustomScriptFileNamingStrategy("sql").createFileName(metadataExtractor.getJavaClass());
      return defaultFileName;
   }

   @Override
   List<String> getResourceFileNames(Method testMethod)
   {
      UsingScript dataAnnotation = getResourceAnnotation(testMethod);
      String[] specifiedFileNames = dataAnnotation.value();
      if (specifiedFileNames.length == 0 || "".equals(specifiedFileNames[0].trim()))
      {
         return Arrays.asList(getDefaultFileName(testMethod));
      }
      return Arrays.asList(specifiedFileNames);
   }

   private UsingScript getResourceAnnotation(Method testMethod)
   {
      return metadataExtractor.usingScript().getUsingPrecedence(testMethod);
   }

   private String getDefaultFileName(Method testMethod)
   {

      if (metadataExtractor.usingScript().isDefinedOn(testMethod))
      {
         return new CustomScriptFileNamingStrategy("sql").createFileName(metadataExtractor.getJavaClass(), testMethod);
      }

      return new CustomScriptFileNamingStrategy("sql").createFileName(metadataExtractor.getJavaClass());
   }

}
