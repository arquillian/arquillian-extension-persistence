package org.jboss.arquillian.persistence.data.dbunit.dataset;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;

public class DataSetRegister
{

   private final List<IDataSet> initial = new ArrayList<IDataSet>();
   
   private final List<IDataSet> expected = new ArrayList<IDataSet>();
   
   public void addInitial(IDataSet initial)
   {
      this.initial.add(initial);
   }
   
   public void addExpected(IDataSet expected)
   {
      this.expected.add(expected);
   }

   public List<IDataSet> getInitial()
   {
      return initial;
   }
   
   public List<IDataSet> getExpected()
   {
      return expected;
   }
   
}
