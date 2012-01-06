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
package org.jboss.arquillian.persistence.data.dbunit.dataset;

import java.io.InputStream;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.dataset.json.JsonDataSet;
import org.jboss.arquillian.persistence.data.dbunit.dataset.yaml.YamlDataSet;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitInitializationException;
import org.jboss.arquillian.persistence.data.descriptor.Format;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DataSetBuilder
{

   private final Format format;

   private DataSetBuilder(Format format)
   {
      this.format = format;
   }

   public IDataSet build(final String file)
   {
      final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
      IDataSet dataSet = null;
      try
      {
         switch (format)
         {
            case XML:
               final FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
               flatXmlDataSetBuilder.setColumnSensing(true);
               dataSet = flatXmlDataSetBuilder.build(inputStream);
               break;
            case EXCEL:
               dataSet = new XlsDataSet(inputStream);
               break;
            case YAML:
               dataSet = new YamlDataSet(inputStream);
               break;
            case JSON:
               dataSet = new JsonDataSet(inputStream);
               break;
            default:
               throw new DBUnitInitializationException("Unsupported data type " + format);
         }
      }
      catch (Exception e)
      {
         throw new DBUnitInitializationException("Unable to load data set from given file: " + file, e);
      }

      return defineReplaceableExpressions(dataSet);
   }

   public static DataSetBuilder builderFor(final Format format)
   {
      return new DataSetBuilder(format);
   }

   private IDataSet defineReplaceableExpressions(IDataSet dataSet)
   {
      final ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
      replacementDataSet.addReplacementObject("[null]", null);
      replacementDataSet.addReplacementObject("[NULL]", null);
      return replacementDataSet;
   }

}
