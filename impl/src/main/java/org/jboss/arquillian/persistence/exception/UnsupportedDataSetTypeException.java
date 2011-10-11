package org.jboss.arquillian.persistence.exception;

public class UnsupportedDataSetTypeException extends RuntimeException
{

   private static final long serialVersionUID = -6305020674826714723L;

   public UnsupportedDataSetTypeException()
   {
   }

   public UnsupportedDataSetTypeException(String message)
   {
      super(message);
   }

   public UnsupportedDataSetTypeException(Throwable cause)
   {
      super(cause);
   }

   public UnsupportedDataSetTypeException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
