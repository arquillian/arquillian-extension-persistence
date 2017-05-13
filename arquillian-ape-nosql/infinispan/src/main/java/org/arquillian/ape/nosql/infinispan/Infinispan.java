package org.arquillian.ape.nosql.infinispan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.arquillian.ape.api.Populator;

/**
 * Annotation to set Populator of Couchbase.
 */
@Populator
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Infinispan {
}
