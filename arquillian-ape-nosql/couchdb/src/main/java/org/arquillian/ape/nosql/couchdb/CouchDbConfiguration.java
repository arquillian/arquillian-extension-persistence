package org.arquillian.ape.nosql.couchdb;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to configure CouchDb connection.
 * All attributes can be set by system property or environment variable by using:
 *
 * ${property:default_value}
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface CouchDbConfiguration {

    String username() default "";
    String password() default "";
    String caching() default "";
    String enableSsl() default "";
    String relaxedSsl() default "";

}
