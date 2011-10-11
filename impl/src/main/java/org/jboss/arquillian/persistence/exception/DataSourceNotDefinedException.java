package org.jboss.arquillian.persistence.exception;

public class DataSourceNotDefinedException extends RuntimeException
{

   private static final long serialVersionUID = 6993144495744705278L;

   public DataSourceNotDefinedException()
   {
   }

   public DataSourceNotDefinedException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public DataSourceNotDefinedException(String message)
   {
      super(message);
   }

   public DataSourceNotDefinedException(Throwable cause)
   {
      super(cause);
   }

}
