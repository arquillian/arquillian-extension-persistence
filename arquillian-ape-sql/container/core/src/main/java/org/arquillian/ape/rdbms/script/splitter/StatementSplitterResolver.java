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
package org.arquillian.ape.rdbms.script.splitter;

import java.util.Collection;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.arquillian.ape.rdbms.util.JavaSPIExtensionLoader;
import org.arquillian.ape.spi.script.StatementSplitter;

public class StatementSplitterResolver {

    private final ScriptingConfiguration scriptingConfiguration;

    public StatementSplitterResolver(ScriptingConfiguration scriptingConfiguration) {
        this.scriptingConfiguration = scriptingConfiguration;
    }

    public StatementSplitter resolve() {
        final String sqlDialect = scriptingConfiguration.getSqlDialect();
        StatementSplitter resolved = null;
        final Collection<StatementSplitter> statementSplitters =
            new JavaSPIExtensionLoader().all(Thread.currentThread().getContextClassLoader(), StatementSplitter.class);
        for (StatementSplitter statementSplitter : statementSplitters) {
            if (statementSplitter.supports().equalsIgnoreCase(sqlDialect)) {
                if (resolved != null) {
                    throw new IllegalStateException(
                        "Found multiple implementations of " + StatementSplitter.class.getName()
                            + " for specified dialect " + sqlDialect);
                }
                resolved = statementSplitter;
                resolved.setStatementDelimiter(scriptingConfiguration.getSqlStatementDelimiter());
                resolved.setTrimStatementDelimiter(scriptingConfiguration.isTrimSqlStatementDelimiter());
            }
        }

        if (resolved == null) {
            throw new IllegalStateException("Unresolvable implementation of "
                + StatementSplitter.class.getName()
                + " for specified dialect "
                + sqlDialect);
        }

        return resolved;
    }
}
