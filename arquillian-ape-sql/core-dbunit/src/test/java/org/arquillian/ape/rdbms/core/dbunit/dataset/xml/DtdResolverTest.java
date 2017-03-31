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
package org.arquillian.ape.rdbms.core.dbunit.dataset.xml;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DtdResolverTest {

    @Test
    public void should_resolve_dtd_from_xml_file() throws Exception {
        // given
        DtdResolver dtdResolver = new DtdResolver();

        // when
        String dtdFile = dtdResolver.resolveDtdLocation("datasets/xml/users-with-dtd.xml");

        // then
        assertThat(dtdFile).isEqualTo("users.dtd");
    }

    @Test
    public void should_resolve_dtd_full_path_from_xml_file() throws Exception {
        // given
        DtdResolver dtdResolver = new DtdResolver();

        // when
        String dtdFile = dtdResolver.resolveDtdLocationFullPath("datasets/xml/users-with-dtd.xml");

        // then
        assertThat(dtdFile).isEqualTo("datasets/xml/users.dtd");
    }

    @Test
    public void should_return_null_when_dtd_not_specified() throws Exception {
        // given
        DtdResolver dtdResolver = new DtdResolver();

        // when
        String dtdFile = dtdResolver.resolveDtdLocation("datasets/xml/users-without-dtd.xml");

        // then
        assertThat(dtdFile).isNull();
    }
}
