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
package org.arquillian.ape.rdbms.script.data.provider;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssert;
import org.arquillian.ape.rdbms.script.data.descriptor.SqlScriptResourceDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlScriptDescriptorAssert extends IterableAssert<SqlScriptResourceDescriptor> {

    protected SqlScriptDescriptorAssert(Iterable<SqlScriptResourceDescriptor> actual) {
        super(actual);
    }

    public static SqlScriptDescriptorAssert assertThat(SqlScriptResourceDescriptor... scriptDescriptors) {
        return new SqlScriptDescriptorAssert(Arrays.asList(scriptDescriptors));
    }

    public static SqlScriptDescriptorAssert assertThat(Iterable<SqlScriptResourceDescriptor> scriptDescriptors) {
        return new SqlScriptDescriptorAssert(scriptDescriptors);
    }

    public SqlScriptDescriptorAssert containsOnlyFollowingFiles(String... files) {
        Assertions.assertThat(extractFileNames()).containsOnly(files);
        return this;
    }

    public SqlScriptDescriptorAssert containsExactlyFollowingFiles(String... files) {
        Assertions.assertThat(extractFileNames()).containsExactly(files);
        return this;
    }

    private List<String> extractFileNames() {
        final List<String> fileNames = new ArrayList<String>();
        for (SqlScriptResourceDescriptor scriptDescriptor : actual) {
            fileNames.add(scriptDescriptor.getLocation());
        }
        return fileNames;
    }

}
