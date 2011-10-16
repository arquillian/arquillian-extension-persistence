package org.jboss.arquillian.persistence.data.dbunit.dataset;

import org.dbunit.dataset.IDataSet;

public class DataSetRegister
{

   private IDataSet initial;
   
   private IDataSet expected;
   
   public void addInitial(IDataSet initial)
   {
      this.initial = initial;
   }
   
   public void addExpected(IDataSet expected)
   {
      this.expected = expected;
   }

   public IDataSet getInitial()
   {
      return initial;
   }
   
   public IDataSet getExpected()
   {
      return expected;
   }
   
}
