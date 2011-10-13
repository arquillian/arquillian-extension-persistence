package org.jboss.arquillian.persistence;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(value={TYPE, METHOD})
@Retention(value=RUNTIME)
@Inherited
public @interface Transactional 
{

   TransactionMode value() default TransactionMode.ROLLBACK;

}
