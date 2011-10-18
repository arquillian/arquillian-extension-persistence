package org.jboss.arquillian.persistence.event;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.core.spi.event.Event;
import org.jboss.arquillian.persistence.data.DataSetDescriptor;

public class DataEvent implements Event
{

   private final List<DataSetDescriptor> dataSetDescriptors;
   
   public DataEvent(List<DataSetDescriptor> dataSDataSetDescriptors)
   {
      this.dataSetDescriptors = new ArrayList<DataSetDescriptor>(dataSDataSetDescriptors);
   }

   public List<DataSetDescriptor> getDataSetDescriptors()
   {
      return dataSetDescriptors;
   }
}
