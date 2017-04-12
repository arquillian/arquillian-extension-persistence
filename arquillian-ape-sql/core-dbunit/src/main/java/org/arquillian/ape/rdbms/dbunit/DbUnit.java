package org.arquillian.ape.rdbms.dbunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.arquillian.ape.api.Populator;

@Populator
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DbUnit {
}
