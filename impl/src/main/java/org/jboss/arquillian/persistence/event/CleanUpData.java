package org.jboss.arquillian.persistence.event;

import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class CleanUpData extends TestEvent
{

   public CleanUpData(TestEvent testEvent)
   {
      super(testEvent.getTestInstance(), testEvent.getTestMethod());
   }

}
