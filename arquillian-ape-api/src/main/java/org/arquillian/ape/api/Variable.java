package org.arquillian.ape.api;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used in Rest populators to set variables to scripts.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@Repeatable(Variables.class)
public @interface Variable {

    String name();

    /**
     *  Attribute can be set by system property or environment variable by using:
     *
     * ${property:default_value}
     * @return
     */
    String value();

}
