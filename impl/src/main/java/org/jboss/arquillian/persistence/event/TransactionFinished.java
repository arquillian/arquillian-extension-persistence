package org.jboss.arquillian.persistence.event;

import java.lang.reflect.Method;

import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class TransactionFinished extends TestEvent
{
   public TransactionFinished(Object testInstance, Method testMethod)
   {
      super(testInstance, testMethod);
   }

   public TransactionFinished(TestEvent testEvent)
   {
      super(testEvent.getTestInstance(), testEvent.getTestMethod());
   }
}
