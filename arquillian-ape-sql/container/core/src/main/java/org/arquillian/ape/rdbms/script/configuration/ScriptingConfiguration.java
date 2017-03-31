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
package org.arquillian.ape.rdbms.script.configuration;

import java.nio.charset.Charset;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.core.configuration.Configuration;
import org.arquillian.ape.rdbms.script.splitter.DefaultStatementSplitter;

import static org.arquillian.ape.rdbms.util.Arrays.copy;

/**
 * Scripting configuration which can be customized in <code>arquillian.xml</code>
 * descriptor in the element with qualifier <code>persistence-script</code>.
 * <br><br>
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class ScriptingConfiguration extends Configuration {

    public static final String NEW_LINE_SYMBOL = "NEW_LINE";
    private static final long serialVersionUID = -7466338979646960512L;
    private String defaultSqlScriptLocation = "scripts/";

    private String[] scriptsToExecuteBeforeTest;

    private String[] scriptsToExecuteAfterTest;

    private TestExecutionPhase defaultCleanupUsingScriptPhase = TestExecutionPhase.AFTER;

    private String sqlStatementDelimiter = ";";

    private boolean showSql = false;

    private String sqlDialect = "default";

    private Charset charset = Charset.forName("UTF-8");

    public ScriptingConfiguration() {
        super("persistence-script", "arquillian.extension.persistence.script.");
    }

    public String[] getScriptsToExecuteBeforeTest() {
        return copy(scriptsToExecuteBeforeTest);
    }

    /**
     * @param scriptsToExecuteBeforeTest
     *     Ad-hoc scripts or file location to be used before every test.
     *     Might be handy for turning off integrity checks.
     */
    public void setScriptsToExecuteBeforeTest(String[] scriptsToExecuteBeforeTest) {
        this.scriptsToExecuteBeforeTest = copy(scriptsToExecuteBeforeTest);
    }

    public String[] getScriptsToExecuteAfterTest() {
        return copy(scriptsToExecuteAfterTest);
    }

    /**
     * @param scriptsToExecuteAfterTest
     *     Ad-hoc scripts or file location to be used after every test.
     *     Could be used to revert operations applied by {@link #scriptsToExecuteBeforeTest}
     */
    public void setScriptsToExecuteAfterTest(String[] scriptsToExecuteAfterTest) {
        this.scriptsToExecuteAfterTest = scriptsToExecuteAfterTest;
    }

    public String getDefaultSqlScriptLocation() {
        return defaultSqlScriptLocation;
    }

    /**
     * @param defaultSqlScriptLocation
     *     Folder where all custom SQL scripts are located.
     *     Default value is <code>scripts</code>
     */
    public void setDefaultSqlScriptLocation(String defaultSqlScriptLocation) {
        this.defaultSqlScriptLocation = defaultSqlScriptLocation;
    }

    public TestExecutionPhase getDefaultCleanupUsingScriptPhase() {
        return defaultCleanupUsingScriptPhase;
    }

    /**
     * @param defaultCleanupUsingScriptPhase
     *     Defines default cleanup phase for custom SQL scripts.
     *     If not specified it's assumed to be AFTER test method.
     */
    public void setDefaultCleanupUsingScriptPhase(TestExecutionPhase defaultCleanupUsingScriptPhase) {
        this.defaultCleanupUsingScriptPhase = defaultCleanupUsingScriptPhase;
    }

    public String getSqlStatementDelimiter() {
        return sqlStatementDelimiter;
    }

    /**
     * @param sqlStatementDelimiter
     *     Defines char sequence indicating end of SQL statement. Default value: ';'
     */
    public void setSqlStatementDelimiter(String sqlStatementDelimiter) {
        this.sqlStatementDelimiter = sqlStatementDelimiter;
    }

    public boolean isShowSql() {
        return showSql;
    }

    /**
     * @param showSql
     *     Defines if each SQL statements should be logged when executing.
     */
    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public String getSqlDialect() {
        return sqlDialect;
    }

    /**
     * Defines which SQL-specific implementation of {@link org.arquillian.persistence.spi.script.StatementSplitter}
     * (parser)
     * should be used when splitting sql script into separated statements. Default value is "default" and {@link
     * DefaultStatementSplitter} is used.
     */
    public void setSqlDialect(String sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    public Charset getCharset() {
        return charset;
    }

    /**
     * Defines which @{link Charset} should be used when executing SQL scripts.
     * <br/>
     * Possible values:
     * <ul>
     * <li>US-ASCII</li>
     * <li>ISO-8859-1</li>
     * <li>UTF-8 (default and strongly recommended)</li>
     * <li>UTF-16</li>
     * <li>UTF-16BE (big endian byte order)</li>
     * <li>UTF-16LE (little endian byte order)</li>
     * </ul>
     * <br/>
     * which are guaranteed to be supported by all Java platform implementations.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
