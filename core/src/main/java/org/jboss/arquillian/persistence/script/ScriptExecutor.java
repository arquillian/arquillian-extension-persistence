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
package org.jboss.arquillian.persistence.script;

import org.jboss.arquillian.persistence.core.exception.ScriptExecutionException;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.persistence.spi.script.StatementSplitter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class ScriptExecutor
{

   private static final Logger log = Logger.getLogger(ScriptExecutor.class.getName());

   private final Connection connection;

   private final ScriptingConfiguration scriptingConfiguration;

   private final StatementSplitter statementSplitter;

   public ScriptExecutor(final Connection connection, final ScriptingConfiguration scriptingConfiguration, final StatementSplitter statementSplitter)
   {
      this.connection = connection;
      this.scriptingConfiguration = scriptingConfiguration;
      this.statementSplitter = statementSplitter;
   }

   public void execute(String script)
   {
      final List<String> statements = statementSplitter.splitStatements(script);

      for (String statement : statements)
      {
         executeStatement(statement);
      }
   }

   void executeStatement(String sqlStatement)
   {
      if (scriptingConfiguration.isShowSql())
      {
         log.info("Executing SQL statement: " + sqlStatement);
      }

      Statement statement = null;
      try
      {
         statement = connection.createStatement();
         statement.execute(sqlStatement);
      }
      catch (Exception e)
      {
         throw new ScriptExecutionException("Unable to execute statement: " + sqlStatement, e);
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
               throw new ScriptExecutionException("Unable to close statement after script execution.", e);
            }
         }
      }
   }

}
