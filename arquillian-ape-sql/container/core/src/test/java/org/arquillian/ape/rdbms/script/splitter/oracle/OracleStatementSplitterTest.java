/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
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
package org.arquillian.ape.rdbms.script.splitter.oracle;

import java.util.List;
import org.arquillian.ape.rdbms.testutils.FileLoader;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OracleStatementSplitterTest {

    @Test
    public void should_split_create_tables_if_exists() throws Exception {
        // given
        final OracleStatementSplitter statementSplitter = new OracleStatementSplitter();
        final String sqlScript = FileLoader.loadAsString("scripts/oracle-create-table-if-exists.sql");

        // when
        final List<String> statements = statementSplitter.splitStatements(sqlScript);

        // then
        assertThat(statements).hasSize(4);
    }

    @Test
    public void should_handle_nested_blocks_as_single_statement() throws Exception {
        // given
        final OracleStatementSplitter splitter = new OracleStatementSplitter();

        // when
        final List<String> statements = splitter.splitStatements("DECLARE\n" +
            "  l_message  \n" +
            "  VARCHAR2 (100) := 'Hello';\n" +
            "BEGIN\n" +
            "  DECLARE\n" +
            "    l_message2     VARCHAR2 (100) := \n" +
            "      l_message || ' World!'; \n" +
            "  BEGIN\n" +
            "    DBMS_OUTPUT.put_line (l_message2);\n" +
            "  END;\n" +
            "EXCEPTION\n" +
            "  WHEN OTHERS\n" +
            "  THEN\n" +
            "    DBMS_OUTPUT.put_line \n" +
            "   (DBMS_UTILITY.format_error_stack);\n" +
            "END;");

        // then
        assertThat(statements).hasSize(1);
    }
}
