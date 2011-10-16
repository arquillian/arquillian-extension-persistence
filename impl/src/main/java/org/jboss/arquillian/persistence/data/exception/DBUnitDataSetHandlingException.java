package org.jboss.arquillian.persistence.data.exception;

public class DBUnitDataSetHandlingException extends RuntimeException
{

   private static final long serialVersionUID = 121044416863086313L;

   public DBUnitDataSetHandlingException(String message)
   {
      super(message);
   }

   public DBUnitDataSetHandlingException(Throwable cause)
   {
      super(cause);
   }

   public DBUnitDataSetHandlingException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
