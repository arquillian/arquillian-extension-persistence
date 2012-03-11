package org.jboss.arquillian.persistence.testextension.event.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.arquillian.persistence.TestExecutionPhase;

@Target(METHOD)
@Retention(RUNTIME)
@Inherited
public @interface CleanupShouldBeTriggered
{

   TestExecutionPhase value();

}
