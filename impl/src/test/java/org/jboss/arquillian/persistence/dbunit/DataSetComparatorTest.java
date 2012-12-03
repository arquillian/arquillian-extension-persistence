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
package org.jboss.arquillian.persistence.dbunit;

import static org.fest.assertions.Assertions.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

import java.util.Arrays;

import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.jboss.arquillian.persistence.core.test.AssertionErrorCollector;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.data.descriptor.Format;
import org.jboss.arquillian.persistence.dbunit.dataset.DataSetBuilder;
import org.junit.Before;
import org.junit.Test;

public class DataSetComparatorTest
{

   private DBUnitConfiguration configuration;

   @Before
   public void setup()
   {
      configuration = new DBUnitConfiguration();
      configuration.setDatatypeFactory(new HsqldbDataTypeFactory());
   }

   @Test
   public void should_map_columns_associated_with_particular_table() throws Exception
   {
      // given
      DataSetComparator dataSetComparator = new DataSetComparator(new String[] {}, new String[] { "table1.id", "table2.name", "table1.test" });

      // then
      assertThat(dataSetComparator.toExclude.columnsPerTable).includes(entry("table1", Arrays.asList("id", "test")), entry("table2", Arrays.asList("name")))
                                                             .hasSize(2);
   }

   @Test
   public void should_map_columns_associated_with_any_table() throws Exception
   {
      // given
      DataSetComparator dataSetComparator = new DataSetComparator(new String[] {}, new String[] { "id", "name" });

      // then
      assertThat(dataSetComparator.toExclude.global).containsOnly("id", "name")
                                                    .hasSize(2);
   }

   @Test
   public void should_map_columns_used_for_all_filtering_and_associated_with_given_table() throws Exception
   {
      // given
      DataSetComparator dataSetComparator = new DataSetComparator(new String[] {}, new String[] { "id", "name", "table.test" });

      // then
      assertThat(dataSetComparator.toExclude.global).containsOnly("id", "name")
                                                    .hasSize(2);
      assertThat(dataSetComparator.toExclude.columnsPerTable).includes(entry("table", Arrays.asList("test")))
                                                             .hasSize(1);
   }

   @Test
   public void should_find_all_differences_between_datasets() throws Exception
   {
      // given
      final AssertionErrorCollector errorCollector = new AssertionErrorCollector();
      DataSetComparator dataSetComparator = new DataSetComparator(new String[] {}, new String[] {});
      IDataSet usersXml = DataSetBuilder.builderFor(Format.XML).build("datasets/users.xml", configuration);
      IDataSet usersYaml = DataSetBuilder.builderFor(Format.YAML).build("datasets/users-modified.yml", configuration);

      // when
      dataSetComparator.compare(usersXml, usersYaml, errorCollector);

      // then
      assertThat(errorCollector.amountOfErrors()).isEqualTo(10);
   }

   @Test
   public void should_find_no_differences_between_identical_datasets() throws Exception
   {
      // given
      final AssertionErrorCollector errorCollector = new AssertionErrorCollector();
      DataSetComparator dataSetComparator = new DataSetComparator(new String[] {}, new String[] {});
      IDataSet usersXml = DataSetBuilder.builderFor(Format.XML).build("datasets/users.xml", configuration);
      IDataSet usersYaml = DataSetBuilder.builderFor(Format.JSON).build("datasets/users.json", configuration);

      // when
      dataSetComparator.compare(usersXml, usersYaml, errorCollector);

      // then
      assertThat(errorCollector.amountOfErrors()).isZero();
   }

   @Test
   public void should_find_no_differences_comparing_the_same_dataset() throws Exception
   {
      // given
      final AssertionErrorCollector errorCollector = new AssertionErrorCollector();
      DataSetComparator dataSetComparator = new DataSetComparator(new String[] {}, new String[] {});
      IDataSet usersXml = DataSetBuilder.builderFor(Format.XML).build("datasets/users.xml", configuration);
      IDataSet usersYaml = DataSetBuilder.builderFor(Format.XML).build("datasets/users.xml", configuration);

      // when
      dataSetComparator.compare(usersXml, usersYaml, errorCollector);

      // then
      assertThat(errorCollector.amountOfErrors()).isZero();
   }

}
