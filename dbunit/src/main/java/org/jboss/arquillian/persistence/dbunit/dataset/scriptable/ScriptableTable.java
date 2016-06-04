/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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
package org.jboss.arquillian.persistence.dbunit.dataset.scriptable;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import org.jboss.arquillian.persistence.core.exception.ScriptableDataSetEvaluationException;
import org.jboss.arquillian.persistence.core.exception.ScriptableDataSetEngineException;

import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Adds support for script language (JSR 223) in table values.
 *
 * @author <a href="mailto:rmpestano@gmail.com">Rafael Pestano</a>
 */
public class ScriptableTable implements ITable {

    //any non digit char followed by ':' followed by 1 or more chars e.g: js: new Date().toString()
    private final Pattern scriptEnginePattern = Pattern.compile(".*\\D.*:.+");

    private static Logger log = Logger.getLogger(ScriptableTable.class.getName());

    private Map<String, ScriptEngine> engines;

    private ScriptEngineManager manager;

    private ITable delegate;


    public ScriptableTable(ITable delegate) {
        this.delegate = delegate;
        engines = new HashMap<String, ScriptEngine>();
        manager = new ScriptEngineManager();
    }

    @Override
    public ITableMetaData getTableMetaData() {
        return delegate.getTableMetaData();
    }

    @Override
    public int getRowCount() {
        return delegate.getRowCount();
    }

    @Override
    public Object getValue(int row, String column) throws DataSetException {
        Object value = delegate.getValue(row, column);
        if (value != null && scriptEnginePattern.matcher(value.toString()).matches()) {
            ScriptEngine engine = getScriptEngine(value.toString().trim());
            if (engine != null) {
                try {
                    return getScriptResult(value.toString(), engine);
                } catch (Exception e) {
                    throw new ScriptableDataSetEvaluationException(String.format("Could not evaluate script expression for table '%s', column '%s'.", getTableMetaData().getTableName(), column));
                }
            }
        }
        return value;
    }


    /**
     * Parses table cell to get script engine
     *
     * @param value the table cell
     * @return scriptEngine
     */
    private ScriptEngine getScriptEngine(String value) {
        String engineName = value.substring(0, value.indexOf(":"));
        if (engines.containsKey(engineName)) {
            return engines.get(engineName);
        } else {
            ScriptEngine engine = manager.getEngineByName(engineName);
            if (engine != null) {
                engines.put(engineName, engine);
            } else {
                throw new ScriptableDataSetEngineException(String.format("Could not find script engine with name %s in classpath", engineName));
            }
            return engine;
        }

    }

    /**
     * Evaluates the script expression
     *
     * @return script expression result
     */
    private Object getScriptResult(String script, ScriptEngine engine) throws ScriptException {
        String scriptToExecute = script.substring(script.indexOf(":") + 1);
        return engine.eval(scriptToExecute);
    }

}
