package org.jboss.arquillian.persistence;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * TODO extend javadoc
 * Loads given data set to the database before test execution. 
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface Data 
{

   /**
    * The file name of the data set used to seed in database. 
    * If not defined it will try to load file named using 
    * following pattern : [full class name].[test method name].[file ext]
    */
   String value() default "";

   Format format() default Format.NOT_DEFINED;
   
}
