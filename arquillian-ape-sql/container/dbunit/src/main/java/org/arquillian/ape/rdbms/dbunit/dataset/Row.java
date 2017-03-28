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
package org.arquillian.ape.rdbms.dbunit.dataset;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents row entry in the data set file.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class Row {

    private final Map<String, String> cells = new HashMap<String, String>();

    public Row(Map<String, String> cells) {
        for (Map.Entry<String, String> cell : cells.entrySet()) {
            this.cells.put(String.valueOf(cell.getKey()), String.valueOf(cell.getValue()));
        }
    }

    public String valueOf(String name) {
        return cells.get(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Row)) {
            return false;
        }

        final Row other = (Row) obj;

        final Map<String, String> otherCells = other.cells;

        if (cells.size() != otherCells.size()) {
            return false;
        }

        for (Map.Entry<String, String> cell : cells.entrySet()) {
            final String name = cell.getKey();
            final String value = cell.getValue();
            if (!value.equals(otherCells.get(name))) {
                return false;
            }
        }

        return true;

    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 1;
        result = prime * result + ((cells == null) ? 0 : cellHashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Row [cells=" + toString(cells) + "]";
    }

    private String toString(Map<String, String> cells2) {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> cell : cells.entrySet()) {
            sb.append("{key = ").append(cell.getKey()).append(", value = ").append(cell.getValue()).append("} ");
        }
        return sb.toString();
    }

    private int cellHashCode() {
        final int prime = 41;
        int result = 1;
        for (Map.Entry<String, String> cell : cells.entrySet()) {
            result = prime * result + cell.getKey().hashCode();
            result = prime * result + cell.getValue().hashCode();
        }
        return result;
    }


}
