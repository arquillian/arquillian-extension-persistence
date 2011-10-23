package org.jboss.arquillian.persistence.event;

import java.lang.reflect.Method;

import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class TransactionStarted extends TestEvent
{

   public TransactionStarted(Object testInstance, Method testMethod)
   {
      super(testInstance, testMethod);
   }
   
   public TransactionStarted(TestEvent testEvent)
   {
      super(testEvent.getTestInstance(), testEvent.getTestMethod());
   }

}
