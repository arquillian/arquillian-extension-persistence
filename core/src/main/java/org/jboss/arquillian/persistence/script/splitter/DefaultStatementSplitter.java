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
package org.jboss.arquillian.persistence.script.splitter;

import org.jboss.arquillian.persistence.script.SpecialCharactersReplacer;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.persistence.spi.script.StatementSplitter;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Splits SQL script into executable sql parts.
 * <p>
 * To some extent based on <a href="http://code.google.com/p/mybatis/source/browse/trunk/src/main/java/org/apache/ibatis/jdbc/ScriptRunner.java?spec=svn5175&r=5175">ScriptRunner</a>
 * from MyBatis project, hence license attribution in the header.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DefaultStatementSplitter implements StatementSplitter {

    private static final String CHAR_SEQUENCE_PATTERN = "(?m)'([^']*)'|\"([^\"]*)\"";

    private static final String ANSI_SQL_COMMENTS_PATTERN = "--.*|//.*|(?s)/\\*.*?\\*/|(?s)\\{.*?}";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private String statementDelimiter;
    
    private boolean trimStatementDelimiter;

    public DefaultStatementSplitter() {
        this.statementDelimiter = ";";
        this.trimStatementDelimiter = false;
    }

    public DefaultStatementSplitter(String statementDelimiter) {
        this.statementDelimiter = statementDelimiter;
    }

    @Override
    public void setStatementDelimiter(String statementDelimiter) {
        this.statementDelimiter = statementDelimiter;
    }
    
    @Override
    public void setTrimStatementDelimiter(boolean trimStatementDelimiter) {
        this.trimStatementDelimiter = trimStatementDelimiter;
    }

    @Override
    public String supports() {
        return "default";
    }

    @Override
    public List<String> splitStatements(String script) {
        script = removeComments(new SpecialCharactersReplacer().escape(script));
        return splitStatements(new StringReader(script));
    }

    @Override
    public List<String> splitStatements(Reader reader) {
        final BufferedReader lineReader = new BufferedReader(reader);
        final List<String> statements = new ArrayList<String>();
        try {
            final StringBuilder readSqlStatement = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                boolean isFullCommand = parseLine(line, readSqlStatement);
                if (isFullCommand) {
                    if (multipleInlineStatements(line)) {
                        statements.addAll(splitInlineStatements(line));
                    } else {
                        final String trimmed = trim(readSqlStatement.toString());
                        if (trimmed.length() > 0) {
                            statements.add(trimmed);
                        }
                    }
                    readSqlStatement.setLength(0);
                }
            }
            if (shouldExecuteRemainingStatements(readSqlStatement)) {
                statements.add(trim(readSqlStatement.toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed parsing file.", e);
        }

        return statements;
    }

    private String removeComments(String script) {
        return script.replaceAll(ANSI_SQL_COMMENTS_PATTERN, "");
    }

    // -- Private methods

    private boolean parseLine(final String line, final StringBuilder sql) {
        String trimmedLine = trimLine(line);
        sql.append(trimmedLine).append(LINE_SEPARATOR);

        return isFullCommand(trimmedLine);
    }

    private String trimLine(final String line) {
        return line.trim() + (isNewLineStatementDelimiter() ? LINE_SEPARATOR : "");
    }

    private boolean shouldExecuteRemainingStatements(final StringBuilder sql) {
        return sql.toString().trim().length() > 0;
    }

    private boolean isNewLineStatementDelimiter() {
        return ScriptingConfiguration.NEW_LINE_SYMBOL.equals(statementDelimiter);
    }

    private List<String> splitInlineStatements(String line) {
        final List<String> statements = new ArrayList<String>();
        final StringTokenizer sqlStatements = new StringTokenizer(line, statementDelimiter);
        while (sqlStatements.hasMoreElements()) {
            final String token = sqlStatements.nextToken();
            statements.add(trim(token));
        }
        return statements;
    }

    private String trim(String line) {
        String trimmed = new SpecialCharactersReplacer().unescape(line.trim());
        if (!lineIsStatementDelimiter(line)) {
            trimmed.replace(LINE_SEPARATOR, " ");

            if (trimStatementDelimiter && trimmed.endsWith(statementDelimiter)) {
                return trimmed.substring(0, trimmed.length() - statementDelimiter.length());
            }
        }
        return trimmed;
    }

    private boolean multipleInlineStatements(String line) {
        if (isNewLineStatementDelimiter()) {
            return false;
        }
        return new StringTokenizer(markCharSequences(line), statementDelimiter).countTokens() > 1;
    }

    private String markCharSequences(String line) {
        return line.replaceAll(CHAR_SEQUENCE_PATTERN, "char_seq");
    }

    private boolean isFullCommand(String line) {
        return lineEndsWithStatementDelimiter(line) || lineIsStatementDelimiter(line);
    }

    private boolean lineIsStatementDelimiter(String line) {
        boolean isStatementDelimiter = line.equals(statementDelimiter);
        if (!isStatementDelimiter && isNewLineStatementDelimiter()) {
            isStatementDelimiter = line.matches("^\\r?\\n$|^\\r$");
        }
        return isStatementDelimiter;
    }

    private boolean lineEndsWithStatementDelimiter(String line) {
        boolean ends = line.endsWith(statementDelimiter);
        if (!ends && isNewLineStatementDelimiter()) {
            ends = line.matches("^.+?\\r?\\n$|^.+?\\r$");
        }
        return ends;
    }
}
