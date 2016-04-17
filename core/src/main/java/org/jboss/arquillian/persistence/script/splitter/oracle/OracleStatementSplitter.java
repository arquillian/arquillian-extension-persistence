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
package org.jboss.arquillian.persistence.script.splitter.oracle;

import org.jboss.arquillian.persistence.spi.script.StatementSplitter;

import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Original code source available under Apache 2.0 License:
 * <a href="https://github.com/gxa/gxa/blob/master/atlas-updates/src/main/java/uk/ac/ebi/gxa/db/OracleScriptSplitter.java">https://github.com/gxa/gxa/blob/master/atlas-updates/src/main/java/uk/ac/ebi/gxa/db/OracleScriptSplitter.java</a>
 * <p>
 * Hugely based on the code from {@link com.carbonfive.db.jdbc.ScriptRunner#doExecute}
 * - the only significant difference is that we support Oracle's dirty hack with slash, <tt>/</tt>,
 * meaning literally "now just send to the database whatever you've got in your
 * <a href="http://download.oracle.com/docs/cd/B19306_01/server.102/b14357/ch4.htm#i1039357">SQL buffer</a>."
 * <p/>
 * For more details, refer to
 * <a href="http://download.oracle.com/docs/cd/B19306_01/server.102/b14357/ch4.htm#i1039663">Oracle documentation</a>
 * on PL/SQL scripts.
 * <p/>
 * Also addresses <a href="http://code.google.com/p/c5-db-migration/issues/detail?id=31">Issue 31</a>:
 * Running Oracle 11g SQL*plus script generates ORA-06650 "end-of-file"
 * of <tt>c5-db-migrations</tt>
 *
 * @author alf
 */
public class OracleStatementSplitter implements StatementSplitter {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private static final String S_N = "(\\s|\\n)+";

    private static final String IDENTIFIER = "(\\S+|\"[^\"]+\")";

    private static final String BLOCK_START = "(^|" + S_N + ")" +
            "create" + S_N +
            "(or" + S_N + "replace" + S_N + ")?" +
            "(function|library|package(" + S_N + "body)?|procedure|trigger|type)" + S_N +
            IDENTIFIER + S_N +
            ".*";

    private final static Pattern BLOCK_START_PATTERN = Pattern.compile(BLOCK_START, Pattern.CASE_INSENSITIVE);

    private String statementDelimiter;

    @Override
    public void setStatementDelimiter(String statementDelimiter) {
        this.statementDelimiter = statementDelimiter;
    }

    @Override
    public String supports() {
        return "oracle";
    }

    @Override
    public List<String> splitStatements(String script) {
        return splitStatements(new StringReader(script));
    }

    @Override
    public List<String> splitStatements(Reader reader) {
        LineNumberReader lineReader = new LineNumberReader(reader);
        StringBuilder sqlBuffer = new StringBuilder();
        ;
        final ArrayList<String> statements = new ArrayList<String>();
        try {
            boolean plSqlMode = false;
            String line;
            while ((line = lineReader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }

                if (line.matches("[/.]")) {
               /*
               http://download.oracle.com/docs/cd/B19306_01/server.102/b14357/ch4.htm#i1039663
               Terminate PL/SQL subprograms by entering a period (.) by itself on a new line.
               You can also terminate and execute a PL/SQL subprogram by entering a slash (/)
               by itself on a new line.
               */
                    statements.add(sqlBuffer.toString());
                    plSqlMode = false;
                    sqlBuffer.setLength(0);
                } else if (!plSqlMode && (BLOCK_START_PATTERN.matcher(sqlBuffer).find() ||
                        "begin".equalsIgnoreCase(line) ||
                        "declare".equalsIgnoreCase(line))) {
                    plSqlMode = true;
                    sqlBuffer.append(line);
                    sqlBuffer.append(LINE_SEPARATOR);
                } else if (!plSqlMode && line.endsWith(";")) {
                    sqlBuffer.append(line.substring(0, line.lastIndexOf(";")));
                    statements.add(sqlBuffer.toString());
                    sqlBuffer.setLength(0);
                } else {
                    sqlBuffer.append(line);
                    sqlBuffer.append(LINE_SEPARATOR);
                }
            }

            if (sqlBuffer != null && sqlBuffer.length() > 0) {
                statements.add(sqlBuffer.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error splitting script.  Cause: " + e, e);
        }

        return statements;
    }

}
