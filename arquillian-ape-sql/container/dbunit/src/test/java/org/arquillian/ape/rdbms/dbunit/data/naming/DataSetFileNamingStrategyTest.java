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
package org.arquillian.ape.rdbms.dbunit.data.naming;

import org.arquillian.ape.rdbms.core.data.descriptor.Format;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DataSetFileNamingStrategyTest {

    @Test
    public void should_produce_default_file_name_of_data_set_for_test_using_full_class_name_and_method_name() throws Exception {
        // given
        DataSetFileNamingStrategy defaultFileNamingStrategy = new DataSetFileNamingStrategy(Format.XML);

        // when
        String fileName = defaultFileNamingStrategy.createFileName(DummyClass.class, DummyClass.class.getMethod("shouldPass"));

        // then
        assertThat(fileName).isEqualTo("org.arquillian.ape.rdbms.dbunit.data.naming.DummyClass#shouldPass.xml");
    }

}
