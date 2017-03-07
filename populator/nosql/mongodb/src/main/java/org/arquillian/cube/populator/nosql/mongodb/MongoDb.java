package org.arquillian.cube.populator.nosql.mongodb;

import org.arquillian.cube.populator.api.Populator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to set Populator of MongoDb.
 */
@Populator
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface MongoDb {
}
