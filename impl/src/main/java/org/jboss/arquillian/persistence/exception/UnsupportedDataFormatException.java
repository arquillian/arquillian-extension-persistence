package org.jboss.arquillian.persistence.exception;

public class UnsupportedDataFormatException extends RuntimeException
{

   private static final long serialVersionUID = -6305020674826714723L;

   public UnsupportedDataFormatException()
   {
   }

   public UnsupportedDataFormatException(String message)
   {
      super(message);
   }

   public UnsupportedDataFormatException(Throwable cause)
   {
      super(cause);
   }

   public UnsupportedDataFormatException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
