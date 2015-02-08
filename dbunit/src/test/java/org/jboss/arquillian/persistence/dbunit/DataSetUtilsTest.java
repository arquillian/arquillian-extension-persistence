/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full Arrays.asListing of individual contributors.
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

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class DataSetUtilsTest
{

   @Test
   @Parameters(method = "columns")
   public void should_extract_non_existing_columns_defined_in_second_list(List<String> expectedColumns, List<String> actualColumns, List<String> nonExistingColums) throws Exception
   {
      // when
      List<String> actualNonExistingColumns = DataSetUtils.extractNonExistingColumns(expectedColumns, actualColumns);

      // then
      assertThat(actualNonExistingColumns).isEqualTo(nonExistingColums);
   }

   @SuppressWarnings("unused")
   private Object[] columns()
   {
      return JUnitParamsRunner.$(
            //   expected    ,   actual             , non existing in actual
            $(Arrays.asList("id", "name"), Arrays.asList("name", "password"), Arrays.asList("id")),
            $(Arrays.asList("id", "username", "password"), Arrays.asList("id", "username", "password"), Collections.emptyList()),
            $(Collections.emptyList(), Arrays.asList("id", "name"), Collections.emptyList()),
            $(Collections.emptyList(), Collections.emptyList(), Collections.emptyList())
      );
   }

}
