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
package org.arquillian.ape.rdbms.core.dbunit.dataset.json;

import java.io.InputStream;
import org.arquillian.ape.rdbms.core.testutils.DataSetAssert;
import org.arquillian.ape.rdbms.core.testutils.FileLoader;
import org.arquillian.ape.rdbms.core.testutils.TableAssert;
import org.junit.After;
import org.junit.Test;

public class JsonDataSetTest {

    private InputStream input;

    @After
    public void closeStream() {
        FileLoader.close(input);
    }

    @Test
    public void should_load_table_from_json_file() throws Exception {
        // given
        input = FileLoader.load("one-table.json");

        // when
        JsonDataSet jsonDataSet = new JsonDataSet(input);

        // then
        DataSetAssert.assertThat(jsonDataSet).hasTables("useraccount");
    }

    @Test
    public void should_load_all_columns_for_table_from_json_file() throws Exception {
        // given
        input = FileLoader.load("one-table.json");

        // when
        JsonDataSet jsonDataSet = new JsonDataSet(input);

        // then
        TableAssert.assertThat(jsonDataSet.getTable("useraccount"))
            .hasColumns("id", "firstname", "lastname", "username", "password", "email");
    }

    @Test
    public void should_load_all_rows_for_table_from_json_file() throws Exception {
        // given
        input = FileLoader.load("one-table.json");

        // when
        JsonDataSet jsonDataSet = new JsonDataSet(input);

        // then
        TableAssert.assertThat(jsonDataSet.getTable("useraccount")).hasRows(2);
    }

    @Test
    public void should_load_all_rows_with_content_for_table_from_json_file() throws Exception {
        // given
        input = FileLoader.load("one-table.json");

        // when
        JsonDataSet jsonDataSet = new JsonDataSet(input);

        // then
        TableAssert.assertThat(jsonDataSet.getTable("useraccount"))
            .hasRow("id: 1", "firstname: John", "lastname: Smith", "username: doovde", "password: password")
            .hasRow("id: 2", "firstname: Clark", "lastname: Kent", "username: superman", "password: kryptonite",
                "email: arquillian@jboss.org");
    }

    @Test
    public void should_load_two_tables_from_json_file() throws Exception {
        // given
        input = FileLoader.load("tables.json");

        // when
        JsonDataSet jsonDataSet = new JsonDataSet(input);

        // then
        DataSetAssert.assertThat(jsonDataSet).hasTables("useraccount", "testtable");
    }
}
