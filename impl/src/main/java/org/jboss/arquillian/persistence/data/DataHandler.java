package org.jboss.arquillian.persistence.data;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.event.CleanUpDataEvent;
import org.jboss.arquillian.persistence.event.PrepareDataEvent;

/**
 * 
 * @author Bartosz Majsak
 *
 */
public interface DataHandler {

    void prepare(@Observes PrepareDataEvent prepareDataEvent);
    
    void cleanup(@Observes CleanUpDataEvent cleanupDataEvent);
    
}
