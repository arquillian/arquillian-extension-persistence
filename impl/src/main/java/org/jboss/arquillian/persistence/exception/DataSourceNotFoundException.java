package org.jboss.arquillian.persistence.exception;

public class DataSourceNotFoundException extends RuntimeException
{

   private static final long serialVersionUID = 8208167112189292935L;

   public DataSourceNotFoundException()
   {
      super();
   }

   public DataSourceNotFoundException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public DataSourceNotFoundException(String message)
   {
      super(message);
   }

   public DataSourceNotFoundException(Throwable cause)
   {
      super(cause);
   }

}
