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
package org.jboss.arquillian.persistence.metadata.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.data.naming.PrefixedScriptFileNamingStrategy;
import org.jboss.arquillian.persistence.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.metadata.ValueExtractor;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class SqlScriptProvider<T extends Annotation> extends ResourceProvider<SqlScriptResourceDescriptor>
{

   private final PersistenceConfiguration configuration;

   private final PrefixedScriptFileNamingStrategy strategy ;

   private final Class<T> annotation;

   private final ValueExtractor<T> extractor;

   SqlScriptProvider(Class<T> annotation, MetadataExtractor metadataExtractor, ValueExtractor<T> extractor, PrefixedScriptFileNamingStrategy scriptFileNamingStrategy, PersistenceConfiguration configuration)
   {
      super(annotation, metadataExtractor);
      this.configuration = configuration;
      this.strategy = scriptFileNamingStrategy;
      this.annotation = annotation;
      this.extractor = extractor;
   }

   public static <K extends Annotation> SqlScriptProviderBuilder<K> forAnnotation(Class<K> annotation)
   {
      return SqlScriptProviderBuilder.<K>create(annotation);
   }

   @Override
   protected SqlScriptResourceDescriptor createDescriptor(String dataFileName)
   {
      return new SqlScriptResourceDescriptor(determineLocation(dataFileName));
   }

   @Override
   protected String defaultLocation()
   {
      return configuration.getDefaultSqlScriptLocation();
   }

   @Override
   protected String defaultFileName()
   {
      String defaultFileName = strategy.createFileName(metadataExtractor.getJavaClass());
      return defaultFileName;
   }

   @Override
   public Collection<String> getResourceFileNames(Method testMethod)
   {
      T annotation = getResourceAnnotation(testMethod);
      String[] specifiedFileNames = extractor.extract(annotation);
      if (specifiedFileNames.length == 0 || "".equals(specifiedFileNames[0].trim()))
      {
         return Arrays.asList(getDefaultFileName(testMethod));
      }
      return Arrays.asList(specifiedFileNames);
   }

   // Fluent builder

   private T getResourceAnnotation(Method testMethod)
   {
      return metadataExtractor.using(annotation).fetchUsingFirst(testMethod);
   }

   private String getDefaultFileName(Method testMethod)
   {

      if (metadataExtractor.using(annotation).isDefinedOn(testMethod))
      {
         return strategy.createFileName(metadataExtractor.getJavaClass(), testMethod);
      }

      return strategy.createFileName(metadataExtractor.getJavaClass());
   }

}
