package org.jboss.arquillian.persistence.event;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.persistence.data.DataSetDescriptor;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class DataEvent extends TestEvent
{

   private final List<DataSetDescriptor> dataSetDescriptors;
   
   public DataEvent(TestEvent testEvent, List<DataSetDescriptor> dataSetDescriptors)
   {
      super(testEvent.getTestInstance(), testEvent.getTestMethod());
      this.dataSetDescriptors = new ArrayList<DataSetDescriptor>(dataSetDescriptors);
   }

   public List<DataSetDescriptor> getDataSetDescriptors()
   {
      return dataSetDescriptors;
   }
}
