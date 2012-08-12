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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.jboss.arquillian.persistence.dbunit.exception.DBUnitDataSetHandlingException;

/**
 *
 * Splits SQL script into executable lines and execute them one by one.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class ScriptExecutor
{

   private static final String SQL_DELIMITER = "(;)|(;(\r)?\n)|((\r)?(\n))";

   private final Connection connection;

   public ScriptExecutor(final Connection connection)
   {
      this.connection = connection;
   }

   public void execute(String script)
   {
      final List<String> statements = splitScriptIntoStatements(script);
      for (String statement : statements)
      {
         executeSingleStatement(statement);
      }
   }

   List<String> splitScriptIntoStatements(String script)
   {
      final Scanner scriptReader = new Scanner(new StringReader(script));
      scriptReader.useDelimiter(SQL_DELIMITER);
      final List<String> statements = new LinkedList<String>();
      while (scriptReader.hasNext())
      {
         final String line = scriptReader.next().trim();
         if (line.length() > 0 && !isComment(line))
         {
            statements.add(line);
         }
      }
      return statements;
   }

   public boolean isComment(final String line)
   {
      return line.startsWith("--");
   }

   private void executeSingleStatement(String line)
   {
      Statement statement = null;
      try
      {
         statement = connection.createStatement();
         statement.execute(line);
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException("Unable to execute line: " + line, e);
      }
      finally
      {
         if (statement != null)
         {
            try
            {
               statement.close();
            }
            catch (SQLException e)
            {
               throw new DBUnitDataSetHandlingException("Unable to close statement after script execution.", e);
            }
         }
      }
   }

}
