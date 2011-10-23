package org.jboss.arquillian.persistence.event;

import java.util.List;

import org.jboss.arquillian.persistence.data.DataSetDescriptor;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class CompareData extends DataEvent
{

   public CompareData(TestEvent testEvent, List<DataSetDescriptor> dataSetDescriptors)
   {
      super(testEvent, dataSetDescriptors);
   }
   
}