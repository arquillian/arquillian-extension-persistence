/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
/*
 *    Copyright 2009-2012 The MyBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.jboss.arquillian.persistence.script;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jboss.arquillian.persistence.dbunit.exception.DBUnitDataSetHandlingException;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;

/**
 *
 * Splits SQL script into executable sql parts and execute them one by one.
 *
 * To some extent based on <a href="http://code.google.com/p/mybatis/source/browse/trunk/src/main/java/org/apache/ibatis/jdbc/ScriptRunner.java?spec=svn5175&r=5175">ScriptRunner</a>
 * from MyBatis project, therefore license attribution
 * in the header.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class ScriptExecutor
{

   private static final String ANSI_SQL_COMMENTS_PATTERN = "--.*|//.*|(?s)/\\\\*.*?\\\\*/|(?s)\\{.*?\\}";

   private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

   private static final String DEFAULT_SQL_DELIMITER = ";";

   private final Connection connection;

   private String statementDelimiter = DEFAULT_SQL_DELIMITER;

   private boolean fullLineDelimiter = false;

   private List<String> statements = new ArrayList<String>();

   public ScriptExecutor(final Connection connection)
   {
      this.connection = connection;
   }

   public void execute(String script)
   {
      try
      {
         script = removeComments(script);
         final BufferedReader lineReader = new BufferedReader(new StringReader(script));
         final StringBuilder readSqlStatement = new StringBuilder();
         String line = null;
         statements.clear();
         while ((line = lineReader.readLine()) != null)
         {
            boolean isFullCommand = parseLine(line, readSqlStatement);
            if (isFullCommand)
            {
               if(multipleInlineStatements(line))
               {
                  splitInlineStatements(line);
               }
               else
               {
                  addStatement(readSqlStatement.toString());
               }
               readSqlStatement.setLength(0);
            }
         }
         if (shouldExecuteRemainingStatements(readSqlStatement))
         {
            addStatement(readSqlStatement.toString());
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed parsing file.", e);
      }

      for (String statement : statements)
      {
         executeStatement(statement);
      }
   }

   private String removeComments(String script)
   {
      return script.replaceAll(ANSI_SQL_COMMENTS_PATTERN, "");
   }

   void executeStatement(String sqlStatement)
   {
      Statement statement = null;
      try
      {
         statement = connection.createStatement();
         statement.execute(sqlStatement);
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException("Unable to execute statement: " + sqlStatement, e);
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

   // -- Private methods

   private boolean parseLine(final String line, final StringBuilder sql)
   {
      String trimmedLine = trim(line);
      sql.append(trimmedLine).append(LINE_SEPARATOR);

      return isFullCommand(trimmedLine);
   }

   private String trim(final String line)
   {
      return line.trim() + (isNewLineStatementDelimiter() ? LINE_SEPARATOR : "");
   }

   private boolean shouldExecuteRemainingStatements(final StringBuilder sql)
   {
      return sql.toString().trim().length() > 0;
   }

   private boolean isNewLineStatementDelimiter()
   {
      return ScriptingConfiguration.NEW_LINE_SYMBOL.equals(getStatementDelimiter());
   }

   private void splitInlineStatements(String line)
   {
      final StringTokenizer sqlStatements = new StringTokenizer(line, getStatementDelimiter());
      while (sqlStatements.hasMoreElements())
      {
         addStatement(sqlStatements.nextToken());
      }
   }

   private void addStatement(String statement)
   {
      statements.add(statement.trim());
   }

   private boolean multipleInlineStatements(String line)
   {
      if (isNewLineStatementDelimiter())
      {
         return false;
      }
      return new StringTokenizer(line, getStatementDelimiter()).countTokens() > 1;
   }

   private boolean isFullCommand(String line)
   {
      return !fullLineDelimiter && lineEndsWithStatementDelimiter(line)
            || fullLineDelimiter && lineIsStatementDelimiter(line);
   }

   private boolean lineIsStatementDelimiter(String line)
   {
      boolean isStatementDelimiter = line.equals(getStatementDelimiter());
      if (!isStatementDelimiter && isNewLineStatementDelimiter())
      {
         isStatementDelimiter = line.matches("^\\r?\\n|\\r$");
      }
      return isStatementDelimiter;
   }

   private boolean lineEndsWithStatementDelimiter(String line)
   {
      boolean ends = line.endsWith(getStatementDelimiter());
      if (!ends && isNewLineStatementDelimiter())
      {
         ends = line.matches("^.+?\\r?\\n|^.+?\\r$");
      }
      return ends;
   }


   // -- Accessors

   public String getStatementDelimiter()
   {
      return statementDelimiter;
   }

   public void setStatementDelimiter(String statementDelimiter)
   {
      this.statementDelimiter = statementDelimiter;
   }

   public boolean isFullLineDelimiter()
   {
      return fullLineDelimiter;
   }

   public void setFullLineDelimiter(boolean fullLineDelimiter)
   {
      this.fullLineDelimiter = fullLineDelimiter;
   }

}
