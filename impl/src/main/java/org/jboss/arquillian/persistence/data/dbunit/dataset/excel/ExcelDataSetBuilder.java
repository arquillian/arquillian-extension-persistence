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
package org.jboss.arquillian.persistence.data.dbunit.dataset.excel;

import java.io.InputStream;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetBuilder;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitInitializationException;

public class ExcelDataSetBuilder extends DataSetBuilder
{

   @Override
   public IDataSet build(String file)
   {
      InputStream xlsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
      IDataSet dataSet = null;
      try
      {
         dataSet = new XlsDataSet(xlsStream);
      }
      catch (Exception e)
      {
         throw new DBUnitInitializationException("Unable to load data set from given file : " + file, e);
      }
      
      return dataSet;
   }

}
