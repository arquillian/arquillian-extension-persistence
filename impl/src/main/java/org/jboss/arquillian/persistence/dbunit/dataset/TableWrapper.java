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

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;

/**
 * Wraps {@link ITable} with specified {@link ITableMetaData} so that DBUnit can recognize 
 * column types of tables from dataset files using specified metadata.
 * This class will be obsolete when the issue(http://sourceforge.net/p/dbunit/bugs/347/)
 * of DBUnit is fixed.
 * 
 * @author <a href="mailto:kunii.masao@gmail.com">Masao Kunii</a>
 * 
 */
public class TableWrapper extends AbstractTable {

	ITable table;
	ITableMetaData metaData;

	public TableWrapper(ITable table, ITableMetaData metaData) {
		this.table = table;
		this.metaData = metaData;
	}

	@Override
	public ITableMetaData getTableMetaData() {
		return metaData;
	}

	@Override
	public int getRowCount() {
		return table.getRowCount();
	}

	@Override
	public Object getValue(int row, String column) throws DataSetException {
		return table.getValue(row, column);
	}
}
