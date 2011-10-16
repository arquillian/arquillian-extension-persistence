package org.jboss.arquillian.persistence.data;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.persistence.event.CompareData;
import org.jboss.arquillian.persistence.event.PrepareData;

/**
 * 
 * @author Bartosz Majsak
 *
 */
public interface DataHandler
{

   void prepare(@Observes PrepareData prepareDataEvent);

   void cleanup(@Observes CleanUpData cleanupDataEvent);

   void compare(@Observes CompareData cleanupDataEvent);

}
