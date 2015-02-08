/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.persistence.testutils;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

public class DataSetAssert extends AbstractAssert<DataSetAssert, IDataSet>
{

   protected DataSetAssert(IDataSet actual)
   {
      super(actual, DataSetAssert.class);
   }

   public DataSetAssert hasTables(String... tables)
   {
      try
      {
         Assertions.assertThat(actual.getTableNames()).contains(tables);
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
      
      return this;
   }

   public static DataSetAssert assertThat(IDataSet dataSet)
   {
      return new DataSetAssert(dataSet);
   }

}
