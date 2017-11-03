package org.arquillian.ape.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used in Rest populators to set URL override in scripts
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface UrlOverride {
    boolean value() default true;
    Class<? extends Annotation> type() default NoType.class;
}
