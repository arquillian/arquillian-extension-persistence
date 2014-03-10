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
package org.jboss.arquillian.persistence.script.data.provider;

import java.lang.annotation.Annotation;

import org.jboss.arquillian.persistence.core.data.naming.FileNamingStrategy;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.core.metadata.ValueExtractor;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class SqlScriptProviderBuilder<K extends Annotation>
{

   public static class Extractor<K extends Annotation>
   {
      private final SqlScriptProviderBuilder<K> builder;

      public Extractor(SqlScriptProviderBuilder<K> builder)
      {
         this.builder = builder;
      }

      public SqlScriptProviderBuilder.Naming<K> extractingMetadataUsing(MetadataExtractor extractor)
      {
         builder.metadataExtractor = extractor;
         return new SqlScriptProviderBuilder.Naming<K>(builder);
      }
   }

   public static class Naming<K extends Annotation>
   {
      private final SqlScriptProviderBuilder<K> builder;

      public Naming(SqlScriptProviderBuilder<K> builder)
      {
         this.builder = builder;
      }

      public SqlScriptProviderBuilder.Builder<K> namingFollows(FileNamingStrategy<String> strategy)
      {
         builder.scriptFileNamingStrategy = strategy;
         return new SqlScriptProviderBuilder.Builder<K>(builder);
      }
   }

   public static class Builder<K extends Annotation>
   {
      private final SqlScriptProviderBuilder<K> builder;

      public Builder(SqlScriptProviderBuilder<K> builder)
      {
         this.builder = builder;
      }

      public SqlScriptProvider<K> build(ValueExtractor<K> extractor)
      {
         return new SqlScriptProvider<K>(builder.annotation, builder.metadataExtractor,
               extractor, builder.scriptFileNamingStrategy, builder.configuration);
      }
   }

   private Class<K> annotation;

   private FileNamingStrategy<String> scriptFileNamingStrategy;

   private MetadataExtractor metadataExtractor;

   private ScriptingConfiguration configuration;

   static <K extends Annotation> SqlScriptProviderBuilder<K> create(Class<K> annotation)
   {
      final SqlScriptProviderBuilder<K> sqlScriptProviderBuilder = new SqlScriptProviderBuilder<K>();
      sqlScriptProviderBuilder.annotation = annotation;
      return sqlScriptProviderBuilder;
   }

   public SqlScriptProviderBuilder.Extractor<K> usingConfiguration(ScriptingConfiguration configuration)
   {
      this.configuration = configuration;
      return new SqlScriptProviderBuilder.Extractor<K>(this);
   }

}