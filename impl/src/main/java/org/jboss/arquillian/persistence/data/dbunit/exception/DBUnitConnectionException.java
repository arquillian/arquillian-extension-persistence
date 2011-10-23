package org.jboss.arquillian.persistence.data.dbunit.exception;

public class DBUnitConnectionException extends RuntimeException
{

   private static final long serialVersionUID = 121044416863086313L;

   public DBUnitConnectionException(String message)
   {
      super(message);
   }

   public DBUnitConnectionException(Throwable cause)
   {
      super(cause);
   }

   public DBUnitConnectionException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
