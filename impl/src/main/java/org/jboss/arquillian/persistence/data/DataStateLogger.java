package org.jboss.arquillian.persistence.data;

import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.persistence.event.PrepareData;

public interface DataStateLogger
{

   void aroundDataSeeding(EventContext<PrepareData> context);

   void aroundCleanup(EventContext<CleanUpData> context);

}
