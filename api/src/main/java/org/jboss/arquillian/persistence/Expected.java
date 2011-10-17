package org.jboss.arquillian.persistence;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Verifies state of underlying data store with the data given by
 * this annotation after test execution.  
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface Expected 
{

   /**
    * The file name of the data set used to seed in database. 
    * In it's not specified explicitly, following strategy is applied:
    * <ul>
    *   <li>Assumption that files are stored in <code>datasets</code> folder.</li>
    *   <li>
    *       If {@link Data} annotation is defined on method level, file name has following format:
    *       <i>expected-[fully qualified class name]#[test method name].[default format]</i>.
    *   </li>
    *   <li>
    *       If {@link Data} annotation is defined on class level, file name has following format:
    *       <i>expected-[fully qualified class name]#.[default format]</i></li>.
    * </ul>
    * <br /><br />
    * If not specified in <code>arquillian.xml</code> expected format is XML.
    */
   String value() default "";

}
