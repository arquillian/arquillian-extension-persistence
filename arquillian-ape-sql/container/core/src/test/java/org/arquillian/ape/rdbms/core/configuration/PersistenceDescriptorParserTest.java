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
package org.arquillian.ape.rdbms.core.configuration;

import org.arquillian.ape.rdbms.core.exception.MultiplePersistenceUnitsException;
import org.arquillian.ape.rdbms.testutils.FileLoader;
import org.junit.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceDescriptorParserTest {

    PersistenceDescriptorParser parser = new PersistenceDescriptorParser();

    @Test
    public void should_read_jta_datasource_name_from_xml_file() throws Exception {
        // given
        final InputStream singleJtaDataSourceDescriptor = FileLoader.load("persistence-jta.xml");

        // when
        String dataSourceName = parser.obtainDataSourceName(singleJtaDataSourceDescriptor);

        // then
        assertThat(dataSourceName).isEqualTo("java:app/datasources/postgresql_ds");
    }

    @Test
    public void should_read_non_jta_datasource_name_from_xml_file() throws Exception {
        // given
        String singleNonJtaDataSourceDescriptor = FileLoader.loadAsString("persistence-non-jta.xml");

        // when
        String dataSourceName = parser.obtainDataSourceName(singleNonJtaDataSourceDescriptor);

        // then
        assertThat(dataSourceName).isEqualTo("java:app/datasources/postgresql_ds_non_jta");
    }

    @Test(expected = MultiplePersistenceUnitsException.class)
    public void should_throw_exception_when_multiple_persistence_units_defined() throws Exception {
        // given
        String multiplePersistenceUnitsDescriptor = FileLoader.loadAsString("persistence-double.xml");

        // when
        String dataSourceName = parser.obtainDataSourceName(multiplePersistenceUnitsDescriptor);

        // then
        // exception should be thrown
    }

}
