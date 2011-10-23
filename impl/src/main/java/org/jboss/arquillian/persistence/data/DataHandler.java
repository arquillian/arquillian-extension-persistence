package org.jboss.arquillian.persistence.data;

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

   void prepare(PrepareData prepareDataEvent);

   void compare(CompareData cleanupDataEvent);

   void cleanup(CleanUpData cleanupDataEvent);

}
