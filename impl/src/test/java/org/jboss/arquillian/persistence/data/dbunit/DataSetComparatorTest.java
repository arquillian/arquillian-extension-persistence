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
package org.jboss.arquillian.persistence.data.dbunit;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.jboss.arquillian.persistence.testutils.CollectionUtils.list;


import org.junit.Test;

public class DataSetComparatorTest
{

   @Test
   public void should_map_columns_associated_with_particular_table() throws Exception
   {
      // given
      DataSetComparator dataSetComparator = new DataSetComparator("table1.id", "table2.name", "table1.test");

      // then
      assertThat(dataSetComparator.columnsPerTableToExclude).includes(entry("table1", list("id", "test")), entry("table2", list("name")))
                                                            .hasSize(2);
   }

   @Test
   public void should_map_columns_associated_with_any_table() throws Exception
   {
      // given
      DataSetComparator dataSetComparator = new DataSetComparator("id", "name");

      // then
      assertThat(dataSetComparator.generalColumnsToExclude).containsOnly("id", "name")
                                                           .hasSize(2);
   }

   @Test
   public void should_map_columns_used_for_all_filtering_and_associated_with_given_table() throws Exception
   {
      // given
      DataSetComparator dataSetComparator = new DataSetComparator("id", "name", "table.test");

      // then
      assertThat(dataSetComparator.generalColumnsToExclude).containsOnly("id", "name")
                                                           .hasSize(2);
      assertThat(dataSetComparator.columnsPerTableToExclude).includes(entry("table", list("test")))
                                                            .hasSize(1);
   }
}
