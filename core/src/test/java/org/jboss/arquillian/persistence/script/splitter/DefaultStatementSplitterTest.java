/*
 * Copyright 2017 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.persistence.script.splitter;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultStatementSplitterTest {

    @Test
    public void should_not_remove_delimiter_if_delimiter_is_not_present() {
        // given
        String script = "SELECT * FROM TEST";

        DefaultStatementSplitter splitter = new DefaultStatementSplitter();
        splitter.setTrimStatementDelimiter(true);

        // when
        List<String> statements = splitter.splitStatements(script);

        // then
        assertThat(statements).containsExactly("SELECT * FROM TEST");
    }

    @Test
    public void should_remove_delimiter_on_single_statement() {
        // given
        String script = "SELECT * FROM TEST;";

        DefaultStatementSplitter splitter = new DefaultStatementSplitter();
        splitter.setTrimStatementDelimiter(true);

        // when
        List<String> statements = splitter.splitStatements(script);

        // then
        assertThat(statements).containsExactly("SELECT * FROM TEST");
    }

    @Test
    public void should_remove_delimiter_on_multiple_statements() {
        // given
        String script = "SELECT * FROM TEST1;SELECT * FROM TEST2;";

        DefaultStatementSplitter splitter = new DefaultStatementSplitter();
        splitter.setTrimStatementDelimiter(true);

        // when
        List<String> statements = splitter.splitStatements(script);

        // then
        assertThat(statements).containsExactly("SELECT * FROM TEST1", "SELECT * FROM TEST2");
    }

    @Test
    public void should_remove_delimiter_on_multiline_statements() {
        // given
        String script = "SELECT * FROM TEST1;\n" +
                "SELECT * FROM TEST2;\n" +
                "SELECT * FROM TEST3;\n";

        DefaultStatementSplitter splitter = new DefaultStatementSplitter();
        splitter.setTrimStatementDelimiter(true);

        // when
        List<String> statements = splitter.splitStatements(script);

        // then
        assertThat(statements).containsExactly("SELECT * FROM TEST1", "SELECT * FROM TEST2", "SELECT * FROM TEST3");
    }

}
