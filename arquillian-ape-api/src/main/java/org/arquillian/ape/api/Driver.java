package org.arquillian.ape.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used by Rdbms systems to configure driver.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@Repeatable(Drivers.class)
public @interface Driver {

    Class<?> value();
    Class<? extends Annotation> type() default NoType.class;

}
