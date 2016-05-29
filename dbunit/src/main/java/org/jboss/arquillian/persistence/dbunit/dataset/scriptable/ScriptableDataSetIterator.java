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
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;

/**
 * @author <a href="mailto:rmpestano@gmail.com">Rafael Pestano</a>
 * 
 */
public class ScriptableDataSetIterator implements ITableIterator{

    private ITableIterator delegate;
    
    public ScriptableDataSetIterator(ITableIterator delegate) {
	this.delegate = delegate;
    }
    
    @Override
    public boolean next() throws DataSetException {
	return delegate.next();
    }

    @Override
    public ITableMetaData getTableMetaData() throws DataSetException {
	return delegate.getTableMetaData();
    }

    @Override
    public ITable getTable() throws DataSetException {
	return new ScriptableTable(delegate.getTable());
    }
    
    

}
