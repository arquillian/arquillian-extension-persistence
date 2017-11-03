package org.arquillian.ape.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used by Rdbms systems to configure username and password.
 *
 * All attributes can be set by system property or environment variable by using:
 *
 * ${property:default_value}
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@Repeatable(Authentications.class)
public @interface Authentication {

    String username();
    String password() default "";
    Class<? extends Annotation> type() default NoType.class;

}
