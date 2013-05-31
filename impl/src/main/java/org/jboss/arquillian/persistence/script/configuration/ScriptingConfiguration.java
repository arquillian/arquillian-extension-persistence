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
package org.jboss.arquillian.persistence.script.configuration;

import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.core.configuration.Configuration;

import java.util.Arrays;

/**
 *
 * Scripting configuration which can be customized in <code>arquillian.xml</code>
 * descriptor in the element with qualifier <code>persistence-script</code>.
 * <br><br>
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class ScriptingConfiguration extends Configuration
{

   private static final long serialVersionUID = -7466338979646960512L;

   public static final String NEW_LINE_SYMBOL = "NEW_LINE";

   private String defaultSqlScriptLocation = "scripts/";

   private String[] scriptsToExecuteBeforeTest;

   private String[] scriptsToExecuteAfterTest;

   private TestExecutionPhase defaultCleanupUsingScriptPhase = TestExecutionPhase.AFTER;

   private String sqlStatementDelimiter = ";";

   public ScriptingConfiguration()
   {
      super("persistence-script", "arquillian.extension.persistence.script.");
   }

   public String[] getScriptsToExecuteBeforeTest()
   {
      return Arrays.copyOf(scriptsToExecuteBeforeTest, scriptsToExecuteBeforeTest.length);
   }

   /**
    * @param scriptsToExecuteBeforeTest Ad-hoc scripts or file location to be used before every test.
    * Might be handy for turning off integrity checks.
    */
   public void setScriptsToExecuteBeforeTest(String[] scriptsToExecuteBeforeTest)
   {
      this.scriptsToExecuteBeforeTest = Arrays.copyOf(scriptsToExecuteBeforeTest, scriptsToExecuteBeforeTest.length);
   }

   public String[] getScriptsToExecuteAfterTest()
   {
      return Arrays.copyOf(scriptsToExecuteAfterTest, scriptsToExecuteAfterTest.length);
   }

   /**
    * @param scriptsToExecuteAfterTest Ad-hoc scripts or file location to be used after every test.
    * Could be used to revert operations applied by {@link #scriptsToExecuteBeforeTest}
    */
   public void setScriptsToExecuteAfterTest(String[] scriptsToExecuteAfterTest)
   {
      this.scriptsToExecuteAfterTest = scriptsToExecuteAfterTest;
   }

   public String getDefaultSqlScriptLocation()
   {
      return defaultSqlScriptLocation;
   }

   /**
    * @param defaultSqlScriptLocation Folder where all custom SQL scripts are located.
    * Default value is <code>scripts</code>
    */
   public void setDefaultSqlScriptLocation(String defaultSqlScriptLocation)
   {
      this.defaultSqlScriptLocation = defaultSqlScriptLocation;
   }

   public TestExecutionPhase getDefaultCleanupUsingScriptPhase()
   {
      return defaultCleanupUsingScriptPhase;
   }

   /**
    * @param defaultCleanupUsingScriptPhase Defines default cleanup phase for custom SQL scripts.
    * If not specified it's assumed to be AFTER test method.
    */
   public void setDefaultCleanupUsingScriptPhase(TestExecutionPhase defaultCleanupUsingScriptPhase)
   {
      this.defaultCleanupUsingScriptPhase = defaultCleanupUsingScriptPhase;
   }

   public String getSqlStatementDelimiter()
   {
      return sqlStatementDelimiter;
   }

   /**
    * @param sqlStatementDelimiter Defines char sequence indicating end of SQL statement. Default value: ';'
    */
   public void setSqlStatementDelimiter(String sqlStatementDelimiter)
   {
      this.sqlStatementDelimiter = sqlStatementDelimiter;
   }

}
