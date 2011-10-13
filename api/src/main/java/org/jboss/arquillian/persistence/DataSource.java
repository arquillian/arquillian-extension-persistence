package org.jboss.arquillian.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines data source to be used for test.
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface DataSource 
{

   String value() default "";

   public static DataSource NOT_DEFINED = new DataSource()
   {

      public Class<? extends Annotation> annotationType()
      {
         return DataSource.class;
      }

      public String value()
      {
         return "-null object pattern-";
      }
   };

}
