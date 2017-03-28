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
package org.arquillian.ape.rdbms.script;

import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.arquillian.ape.rdbms.script.splitter.DefaultStatementSplitter;
import org.arquillian.ape.rdbms.testutils.FileLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ScriptExecutorTest {

    private ScriptExecutor scriptExecutor;

    private ScriptingConfiguration scriptingConfiguration;

    @Mock
    private Connection connection;

    @Before
    public void initializeScriptExecutor() throws SQLException {
        scriptingConfiguration = new ScriptingConfiguration();
        scriptExecutor = spy(new ScriptExecutor(connection, scriptingConfiguration, new DefaultStatementSplitter(";")));
        when(connection.createStatement()).thenReturn(mock(Statement.class));
    }

    @Test
    public void should_execute_two_statements_when_script_contains_two_insert_statements() throws Exception {
        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/two-inserts.sql"));

        // then
        verify(connection, times(2)).createStatement();
    }

    @Test
    public void should_execute_two_statements_when_script_contains_two_insert_statements_and_comment() throws Exception {
        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/two-inserts-with-comment.sql"));

        // then
        verify(connection, times(2)).createStatement();
    }

    @Test
    public void should_not_execute_any_statements_when_script_contains_only_comments() throws Exception {
        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/just-comments.sql"));

        // then
        verify(connection, times(0)).createStatement();
    }

    @Test
    public void should_execute_three_statements_from_inline_script_as_three_seperated_statements() throws Exception {
        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/three-inserts-in-one-line.sql"));

        // then
        verify(connection, times(3)).createStatement();
    }

    @Test
    public void should_execute_single_statement_spanned_across_multiple_lines() throws Exception {
        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/one-insert-multiple-line.sql"));

        // then
        verify(connection, times(1)).createStatement();
    }

    @Test
    public void should_execute_mysql_script() throws Exception {
        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/mysql-script.sql"));

        // then
        verify(connection, times(16)).createStatement();
    }

    @Test
    public void should_execute_statements_with_custom_delimiter() throws Exception {
        // given
        scriptingConfiguration.setSqlStatementDelimiter("GO");
        scriptExecutor = spy(new ScriptExecutor(connection, scriptingConfiguration, new DefaultStatementSplitter("GO")));

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/t-sql-example.sql"));

        // then
        verify(connection, times(4)).createStatement();
    }


    @Test
    public void should_execute_script_with_new_line_as_statement_delimiter() throws Exception {
        // given
        scriptingConfiguration.setSqlStatementDelimiter(ScriptingConfiguration.NEW_LINE_SYMBOL);
        scriptExecutor = spy(new ScriptExecutor(connection, scriptingConfiguration, new DefaultStatementSplitter(ScriptingConfiguration.NEW_LINE_SYMBOL)));

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/new-line-delimiter.sql"));

        // then
        verify(connection, times(6)).createStatement();
    }

    @Test
    public void should_execute_script_with_new_line_as_statement_delimiter_ommiting_trailing_comments() throws Exception {
        // given
        ArgumentCaptor<String> statementsCaptor = ArgumentCaptor.forClass(String.class);
        scriptingConfiguration.setSqlStatementDelimiter(ScriptingConfiguration.NEW_LINE_SYMBOL);
        scriptExecutor = spy(new ScriptExecutor(connection, scriptingConfiguration, new DefaultStatementSplitter(ScriptingConfiguration.NEW_LINE_SYMBOL)));

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/new-line-delimiter-with-trailing-comments.sql"));

        // then
        verify(connection, times(6)).createStatement();
        verify(connection.createStatement(), times(6)).execute(statementsCaptor.capture());
        assertThat(statementsCaptor.getAllValues()).containsSequence("create table address (id bigint not null, city varchar(255) not null, houseNumber integer, streetName varchar(255) not null, version bigint, zipCode integer not null, primary key (id))",
                "create table useraccount (id bigint generated by default as identity, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(255) not null, openDate date, username varchar(32) not null, primary key (id))",
                "create table useraccount_address (useraccount_id bigint not null, addresses_id bigint not null, primary key (useraccount_id, addresses_id), unique (addresses_id))",
                "alter table useraccount_address add constraint FK538F4B7EC498202 foreign key (useraccount_id) references useraccount",
                "alter table useraccount_address add constraint FK538F4B757E57A74 foreign key (addresses_id) references address",
                "create sequence hibernate_sequence start with 1 increment by 1");
    }

    @Test
    public void should_execute_the_whole_script_if_delimiter_not_found() throws Exception {
        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/mssql-identity-insert-off.sql"));

        // then
        verify(connection, times(1)).createStatement();
    }

    @Test
    public void should_execute_script_with_begin_and_end() throws Exception {
        // given
        ArgumentCaptor<String> statementsCaptor = ArgumentCaptor.forClass(String.class);
        scriptExecutor = spy(new ScriptExecutor(connection, scriptingConfiguration, new DefaultStatementSplitter(ScriptingConfiguration.NEW_LINE_SYMBOL)));
        scriptingConfiguration.setSqlStatementDelimiter(ScriptingConfiguration.NEW_LINE_SYMBOL);

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/new-line-delimiter-begin-end.sql"));

        // then
        verify(connection, times(8)).createStatement();
        verify(connection.createStatement(), times(8)).execute(statementsCaptor.capture());
        assertThat(statementsCaptor.getAllValues())
                .containsSequence(
                        "BEGIN",
                        "create table address (id bigint not null, city varchar(255) not null, houseNumber integer, streetName varchar(255) not null, version bigint, zipCode integer not null, primary key (id))",
                        "create table useraccount (id bigint generated by default as identity, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(255) not null, openDate date, username varchar(32) not null, primary key (id))",
                        "create table useraccount_address (useraccount_id bigint not null, addresses_id bigint not null, primary key (useraccount_id, addresses_id), unique (addresses_id))",
                        "alter table useraccount_address add constraint FK538F4B7EC498202 foreign key (useraccount_id) references useraccount",
                        "alter table useraccount_address add constraint FK538F4B757E57A74 foreign key (addresses_id) references address",
                        "create sequence hibernate_sequence start with 1 increment by 1",
                        "END");
    }

    @Test
    public void should_execute_script_with_multiple_line_comment() throws Exception {
        // given
        ArgumentCaptor<String> statementsCaptor = ArgumentCaptor.forClass(String.class);
        scriptingConfiguration.setSqlStatementDelimiter(ScriptingConfiguration.NEW_LINE_SYMBOL);
        scriptExecutor = spy(new ScriptExecutor(connection, scriptingConfiguration, new DefaultStatementSplitter(ScriptingConfiguration.NEW_LINE_SYMBOL)));

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/new-line-delimiter-with-multiple-line-comment.sql"));

        // then
        verify(connection, times(8)).createStatement();
        verify(connection.createStatement(), times(8)).execute(statementsCaptor.capture());
        assertThat(statementsCaptor.getAllValues())
                .containsSequence(
                        "BEGIN",
                        "create table address (id bigint not null, city varchar(255) not null, houseNumber integer, streetName varchar(255) not null, version bigint, zipCode integer not null, primary key (id))",
                        "create table useraccount (id bigint generated by default as identity, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(255) not null, openDate date, username varchar(32) not null, primary key (id))",
                        "create table useraccount_address (useraccount_id bigint not null, addresses_id bigint not null, primary key (useraccount_id, addresses_id), unique (addresses_id))",
                        "alter table useraccount_address add constraint FK538F4B7EC498202 foreign key (useraccount_id) references useraccount",
                        "alter table useraccount_address add constraint FK538F4B757E57A74 foreign key (addresses_id) references address",
                        "create sequence hibernate_sequence start with 1 increment by 1",
                        "END");
    }

    @Test
    public void should_insert_slash_as_value() throws Exception {
        // given
        ArgumentCaptor<String> statementsCaptor = ArgumentCaptor.forClass(String.class);
        scriptingConfiguration.setSqlStatementDelimiter(ScriptingConfiguration.NEW_LINE_SYMBOL);

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/insert-slash.sql"));

        // then
        verify(connection, times(1)).createStatement();
        verify(connection.createStatement(), times(1)).execute(statementsCaptor.capture());
        assertThat(statementsCaptor.getAllValues()).containsSequence(
                "insert into useraccount (id, firstname, lastname, username, password)" +
                        " values  (1, 'John', 'Smith', 'doovde', 'pa/ss/wo/rd')"
        );
    }

    @Test
    public void should_insert_special_entities() throws Exception {
        // given
        ArgumentCaptor<String> statementsCaptor = ArgumentCaptor.forClass(String.class);

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/insert-html-special-chars.sql"));

        // then
        verify(connection, times(1)).createStatement();
        verify(connection.createStatement(), times(1)).execute(statementsCaptor.capture());
        assertThat(statementsCaptor.getAllValues()).containsSequence(
                "insert into useraccount (id, firstname, lastname, username, password)" +
                        " values (1, 'John', 'Smith & Company', 'doovde;;', '&amp;test&copy;');"
        );
    }

    @Test
    public void should_insert_special_entities_with_custom_end_line() throws Exception {
        // given
        ArgumentCaptor<String> statementsCaptor = ArgumentCaptor.forClass(String.class);
        scriptingConfiguration.setSqlStatementDelimiter("GO");

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/insert-html-special-chars-with-custom-end-line.sql"));

        // then
        verify(connection, times(1)).createStatement();
        verify(connection.createStatement(), times(1)).execute(statementsCaptor.capture());
        assertThat(statementsCaptor.getAllValues()).containsSequence(
                "insert into useraccount (id, firstname, lastname, username, password)" +
                        " values (1, 'John', 'Smith & Company', 'doovde;;', '&amp;test&copy;')\nGO"
        );
    }

    @Test
    public void should_insert_xml_tags() throws Exception {
        // given
        ArgumentCaptor<String> statementsCaptor = ArgumentCaptor.forClass(String.class);

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/insert-with-xml.sql"));

        // then
        verify(connection, times(2)).createStatement();
        verify(connection.createStatement(), times(2)).execute(statementsCaptor.capture());
        assertThat(statementsCaptor.getAllValues()).containsSequence(
                "insert into useraccount (id, firstname, lastname, username, password)" +
                        " values (1, 'John', 'Smith', 'doovde', '<strong>vodka</strong>');",
                "insert into useraccount (id, firstname, lastname, username, password)" +
                        " values (2, 'John', 'Sharp', 'asdqwe', 'closing only</strong>');"
        );
    }

    @Test
    public void should_insert_xml_tags_new_line_as_statement_delimiter() throws Exception {
        // given
        ArgumentCaptor<String> statementsCaptor = ArgumentCaptor.forClass(String.class);
        scriptingConfiguration.setSqlStatementDelimiter(ScriptingConfiguration.NEW_LINE_SYMBOL);
        scriptExecutor = spy(new ScriptExecutor(connection, scriptingConfiguration, new DefaultStatementSplitter(ScriptingConfiguration.NEW_LINE_SYMBOL)));

        // when
        scriptExecutor.execute(FileLoader.loadAsString("scripts/insert-with-xml-new-line.sql"));

        // then
        verify(connection, times(2)).createStatement();
        verify(connection.createStatement(), times(2)).execute(statementsCaptor.capture());
        assertThat(statementsCaptor.getAllValues()).containsSequence(
                "insert into useraccount (id, firstname, lastname, username, password)" +
                        " values (1, 'John', 'Smith', 'doovde', '<strong>vodka</strong>')",
                "insert into useraccount (id, firstname, lastname, username, password)" +
                        " values (2, 'John', 'Sharp', 'asdqwe', 'closing only</strong>')"
        );
    }

}
