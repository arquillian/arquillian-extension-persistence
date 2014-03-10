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
package org.jboss.arquillian.persistence.dbunit.dataset;

import java.io.IOException;
import java.io.InputStream;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.jboss.arquillian.persistence.core.data.descriptor.Format;
import org.jboss.arquillian.persistence.dbunit.dataset.json.JsonDataSet;
import org.jboss.arquillian.persistence.dbunit.dataset.xml.DtdResolver;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSet;
import org.jboss.arquillian.persistence.dbunit.exception.DBUnitInitializationException;
import org.yaml.snakeyaml.Yaml;

import static org.jboss.arquillian.persistence.core.data.descriptor.Format.*;

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
      IDataSet dataSet;
      try
      {
         switch (format)
         {
            case XML:
               dataSet = loadXmlDataSet(file);
               break;
            case EXCEL:
               dataSet = loadExcelDataSet(file);
               break;
            case YAML:
               dataSet = loadYamlDataSet(file);
               break;
            case JSON:
               dataSet = loadJsonDataSet(file);
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

   // Private methods

   private IDataSet loadXmlDataSet(final String xmlFile) throws DataSetException
   {
      final FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
      flatXmlDataSetBuilder.setColumnSensing(true);
      addDtdIfDefined(flatXmlDataSetBuilder, xmlFile);
      return flatXmlDataSetBuilder.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFile));
   }

   private void addDtdIfDefined(final FlatXmlDataSetBuilder flatXmlDataSetBuilder, final String xmlFile)
   {
      String dtd = new DtdResolver().resolveDtdLocationFullPath(xmlFile);
      if (dtd != null)
      {
         try
         {
            flatXmlDataSetBuilder.setMetaDataSetFromDtd(Thread.currentThread().getContextClassLoader().getResourceAsStream(dtd));
         }
         catch (DataSetException e)
         {
            throw new DBUnitInitializationException("Unable to attach DTD " + dtd + " defined for " + xmlFile, e);
         }
         catch (IOException e)
         {
            throw new DBUnitInitializationException("Unable to attach DTD " + dtd + " defined for " + xmlFile, e);
         }
      }
   }

   private XlsDataSet loadExcelDataSet(final String file) throws IOException, DataSetException
   {
      final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
      return new XlsDataSet(inputStream);
   }

   private JsonDataSet loadJsonDataSet(final String file) throws DataSetException
   {
      final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
      return new JsonDataSet(inputStream);
   }

   private IDataSet loadYamlDataSet(final String file) throws IOException,
         DataSetException
   {
      IDataSet dataSet;
      if (isYamlEmpty(file))
      {
         dataSet = new DefaultDataSet();
      }
      else
      {
         final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
         dataSet = new YamlDataSet(inputStream);
         if (inputStream != null)
         {
            inputStream.close();
         }
      }
      return dataSet;
   }


   private boolean isYamlEmpty(final String yamlFile) throws IOException
   {
      final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(yamlFile);
      final boolean isEmpty = new Yaml().load(inputStream) == null;
      if (inputStream != null)
      {
         inputStream.close();
      }
      return isEmpty;
   }

   private IDataSet defineReplaceableExpressions(IDataSet dataSet)
   {
      final ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
      replacementDataSet.addReplacementObject("[null]", null);
      replacementDataSet.addReplacementObject("[NULL]", null);
      return replacementDataSet;
   }

}
