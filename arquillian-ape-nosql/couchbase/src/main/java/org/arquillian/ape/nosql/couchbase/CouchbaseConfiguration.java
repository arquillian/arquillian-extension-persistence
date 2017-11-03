package org.arquillian.ape.nosql.couchbase;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to configure Couchbase connection.
 * All attributes can be set by system property or environment variable by using:
 *
 * ${property:default_value}
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface CouchbaseConfiguration {

    String bucketPassword() default "";
    String createBucket() default "";
    String clusterUsername() default "";
    String clusterPassword() default "";

}
