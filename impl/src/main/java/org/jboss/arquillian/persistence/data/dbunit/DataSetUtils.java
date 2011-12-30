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
package org.jboss.arquillian.persistence.data.dbunit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DataSetUtils
{

   public static IDataSet mergeDataSets(List<IDataSet> dataSets) throws DataSetException
   {
      return new CompositeDataSet(dataSets.toArray(new IDataSet[dataSets.size()]));
   }

   public static String[] columnsNotSpecifiedInExpectedDataSet(ITable expectedTableState, ITable currentTableState) throws DataSetException
   {
      final List<String> columnsNotSpecifiedInExpectedDataSet = new ArrayList<String>();
      final Set<Column> allColumns = new HashSet<Column>(Arrays.asList(currentTableState.getTableMetaData().getColumns()));
      final Set<String> expectedColumnNames = new HashSet<String>(extractColumnNames(expectedTableState.getTableMetaData().getColumns()));

      for (Column column : allColumns)
      {
         if (!expectedColumnNames.contains(column.getColumnName().toLowerCase()))
         {
            columnsNotSpecifiedInExpectedDataSet.add(column.getColumnName().toLowerCase());
         }
      }

      return columnsNotSpecifiedInExpectedDataSet.toArray(new String[columnsNotSpecifiedInExpectedDataSet.size()]);
   }

   private static List<String> extractColumnNames(Column[] columns)
   {
      final List<String> names = new ArrayList<String>();
      for (Column column : columns)
      {
         names.add(column.getColumnName().toLowerCase());
      }
      return names;
   }

}
