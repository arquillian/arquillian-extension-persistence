package org.jboss.arquillian.persistence.dbunit.dataset.scriptable;

import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;

/**
 * @author <a href="mailto:rmpestano@gmail.com">Rafael Pestano</a>
 * 
 */
public class ScriptableDataSet extends AbstractDataSet {

    private IDataSet delegate;

    public ScriptableDataSet(IDataSet delegate) {
	this.delegate = delegate;
    }

    @Override
    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
	return new ScriptableDataSetIterator(reversed ? delegate.reverseIterator() : delegate.iterator());
    }

}
