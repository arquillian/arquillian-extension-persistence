package org.arquillian.ape.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@Repeatable(Uris.class)
public @interface Uri {

    String value();
    String storage() default "";
    Class<? extends Annotation> type() default NoType.class;

}
