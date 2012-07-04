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
package org.jboss.arquillian.persistence.core.data.script;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import org.jboss.arquillian.persistence.core.data.script.ScriptExecutor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ScriptExecutorTest
{

   ScriptExecutor scriptExecutor;

   @Mock
   Connection connection;


   @Before
   public void initializeScriptExecutor() throws SQLException
   {
      scriptExecutor = new ScriptExecutor(connection);
      when(connection.createStatement()).thenReturn(mock(Statement.class));
   }

   @Test
   public void should_execute_two_statements_when_script_contains_two_insert_statements() throws Exception
   {
      // when
      scriptExecutor.execute(loadFileAsString("scripts/two-inserts.sql"));

      // then
      verify(connection, times(2)).createStatement();
   }

   @Test
   public void should_execute_two_statements_when_script_contains_two_insert_statements_and_comment() throws Exception
   {
      // when
      scriptExecutor.execute(loadFileAsString("scripts/two-inserts-with-comment.sql"));

      // then
      verify(connection, times(2)).createStatement();
   }

   @Test
   public void should_not_execute_any_statements_when_script_contains_only_comments() throws Exception
   {
      // when
      scriptExecutor.execute(loadFileAsString("scripts/just-comments.sql"));

      // then
      verify(connection, times(0)).createStatement();
   }

   @Test
   public void should_execute_three_statements_from_inline_script() throws Exception
   {
      // when
      scriptExecutor.execute(loadFileAsString("scripts/three-inserts-in-one-line.sql"));

      // then
      verify(connection, times(3)).createStatement();
   }

   @Test
   public void should_split_into_three_statements_from_inline_script() throws Exception
   {
      // when
      List<String> statements = scriptExecutor.splitScriptIntoStatements(loadFileAsString("scripts/three-inserts-in-one-line.sql"));

      // then
      assertThat(statements).containsSequence("INSERT INTO useraccount (id, firstname, lastname, username, password) VALUES (1, 'John', 'Smith', 'doovde', 'password')",
            "INSERT INTO useraccount (id, firstname, lastname, username, password) VALUES (2, 'Clark', 'Kent', 'superman', 'kryptonite')",
            "INSERT INTO useraccount (id, firstname, lastname, username, password) VALUES (3, 'Cole', 'MacGrath', 'infamous', 'kessler')");
   }

   // -- Private utility method

   private String loadFileAsString(final String scriptFile)
   {
      final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(scriptFile);
      return new Scanner(resourceAsStream).useDelimiter("\\A").next();
   }

}
