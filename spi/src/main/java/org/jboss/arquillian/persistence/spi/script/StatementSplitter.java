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
package org.jboss.arquillian.persistence.spi.script;

import java.io.Reader;
import java.util.List;

/**
 * By providing concrete implementation you can specify parsing logic for given SQL dialect
 * and splits the script into executable sql parts.
 * <p>
 * Custom implementation can be bundled with the test class (for example as separated JAR) and should have
 * proper SPI entry in META-INF/services.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public interface StatementSplitter {
    /**
     * Defines the name of supported database dialect. Used in the Arquillian scripting configuration
     * to determine which implementation should be used when parsing SQL scripts.
     *
     * @return
     */
    String supports();

    /**
     * Defines statement delimiter
     *
     * @param statementDelimiter
     */
    void setStatementDelimiter(String statementDelimiter);

    /**
     * Splits given script into executable statements
     *
     * @param script
     * @return
     */
    List<String> splitStatements(String script);

    /**
     * Splits given script into executable statements
     *
     * @param reader
     * @return
     */
    List<String> splitStatements(Reader reader);
}
