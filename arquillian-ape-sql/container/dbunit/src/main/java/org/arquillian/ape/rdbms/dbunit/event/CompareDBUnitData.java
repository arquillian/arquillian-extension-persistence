/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.ape.rdbms.dbunit.event;

import org.dbunit.dataset.filter.IColumnFilter;
import org.arquillian.ape.rdbms.core.event.DataEvent;
import org.arquillian.ape.rdbms.dbunit.data.descriptor.DataSetResourceDescriptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.arquillian.ape.rdbms.util.Arrays.copy;

public class CompareDBUnitData extends DataEvent<DataSetResourceDescriptor> {

    private final String[] columnsToExclude;

    private final String[] sortByColumns;

    private final Set<Class<? extends IColumnFilter>> customColumnFilters = new HashSet<Class<? extends IColumnFilter>>();

    public CompareDBUnitData(Collection<DataSetResourceDescriptor> dataSetDescriptors, String[] sortByColumns, String[] columnsToExclude) {
        super(dataSetDescriptors);
        this.columnsToExclude = columnsToExclude;
        this.sortByColumns = sortByColumns;
    }

    public boolean hasFilters() {
        return !customColumnFilters.isEmpty();
    }

    public void add(Class<? extends IColumnFilter>... filters) {
        customColumnFilters.addAll(Arrays.asList(filters));
    }

    public String[] getColumnsToExclude() {
        return copy(columnsToExclude);
    }

    public String[] getSortByColumns() {
        return copy(sortByColumns);
    }

    public Set<Class<? extends IColumnFilter>> getCustomColumnFilters() {
        return customColumnFilters;
    }

}
