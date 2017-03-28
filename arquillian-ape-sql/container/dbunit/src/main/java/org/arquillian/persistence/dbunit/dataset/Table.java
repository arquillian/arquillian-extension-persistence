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
package org.jboss.arquillian.persistence.dbunit.dataset;

import java.util.*;

/**
 * Represents table described in data set. Stores information such as
 * table's name, list of columns and {@link Row}s.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class Table {
    private final String tableName;

    private final Set<String> columns = new HashSet<String>();

    private final List<Row> rows = new ArrayList<Row>();

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public void addRows(Collection<Row> rows) {
        this.rows.addAll(rows);
    }

    public void addColumns(Collection<String> columns) {
        this.columns.addAll(columns);
    }

    public String getTableName() {
        return tableName;
    }

    public Set<String> getColumns() {
        return Collections.unmodifiableSet(columns);
    }

    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }

}
