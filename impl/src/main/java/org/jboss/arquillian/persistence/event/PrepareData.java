package org.jboss.arquillian.persistence.event;

import java.util.List;

import org.jboss.arquillian.persistence.data.DataSetDescriptor;

public class PrepareData extends DataEvent
{

   public PrepareData(List<DataSetDescriptor> dataSDataSetDescriptors)
   {
      super(dataSDataSetDescriptors);
   }
   
}
