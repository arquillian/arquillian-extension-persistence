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
package org.jboss.arquillian.persistence.dbunit.dataset.scriptable;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.jboss.arquillian.persistence.dbunit.dataset.json.JsonDataSet;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSet;
import org.jboss.arquillian.persistence.testutils.FileLoader;
import org.jboss.arquillian.persistence.testutils.TableAssert;
import org.junit.After;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;

public class ScriptableDataSetTest {

    InputStream input;

    @After
    public void closeStream() {
        FileLoader.close(input);
    }

    @Test
    public void should_load_all_rows_with_script_content_from_yaml_file() throws Exception {
        // given
        input = FileLoader.load("scriptable.yml");

        // when
        IDataSet yamlDataSet = new YamlDataSet(input);
        yamlDataSet = new ScriptableDataSet(yamlDataSet);

        // then
        TableAssert.assertThat(yamlDataSet.getTable("useraccount"))
                .hasRow("id: 1", "firstname: John", "lastname: Smith", "username: doovde", "password: password", "age: 42.0")
                .hasRow("id: 2", "firstname: Clark", "lastname: Kent", "username: superman", "password: kryptonite", "email: arquillian@jboss.org", "birthdate: "+new Date());
    }

    @Test
    public void should_load_all_rows_with_script_content_from_json_file() throws Exception {
        // given
        input = FileLoader.load("scriptable.json");

        // when
        IDataSet jsonDataSet = new JsonDataSet(input);
        jsonDataSet = new ScriptableDataSet(jsonDataSet);

        // then
        TableAssert.assertThat(jsonDataSet.getTable("useraccount"))
                .hasRow("id: 1", "firstname: John", "lastname: Smith", "username: doovde", "password: password", "age: 42.0")
                .hasRow("id: 2", "firstname: Clark", "lastname: Kent", "username: superman", "password: kryptonite", "email: arquillian@jboss.org", "birthdate: "+new Date());
    }

    @Test
    public void should_load_all_rows_with_script_content_from_xml_file() throws Exception {
        // given
        input = FileLoader.load("scriptable.xml");

        // when
        IDataSet xmlDataSet = new FlatXmlDataSetBuilder().build(input);
        xmlDataSet = new ScriptableDataSet(xmlDataSet);

        // then
        TableAssert.assertThat(xmlDataSet.getTable("useraccount"))
                .hasRow("id: 1", "firstname: John", "lastname: Smith", "username: doovde", "password: password", "age: 42.0", "email: \"\" ", "birthdate: \"\" ")
                .hasRow("id: 2", "firstname: Clark", "lastname: Kent", "username: superman", "password: kryptonite", "age: \"\" ", "email: arquillian@jboss.org", "birthdate: "+new Date());
    }

}
