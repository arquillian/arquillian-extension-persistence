package org.arquillian.cube.populator.spi;


/**
 * Base class tha all integrations with Populator must implements.
 */
public interface PopulatorService<T> {

   /**
    * Get annotation used in enricher to inject service backed by this implementation.
    * This annotation must be meta-annotated with org.arquillian.cube.populator.api.Populator annotation.
    * @return Annotation used for identifying this service.
    */
   Class<T> getPopulatorAnnotation();

}
