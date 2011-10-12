package org.jboss.arquillian.persistence.data.dbunit;

public class DBUnitInitializationException extends RuntimeException
{

   private static final long serialVersionUID = -7080488005712280266L;

   public DBUnitInitializationException()
   {
      super();
   }

   public DBUnitInitializationException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public DBUnitInitializationException(String message)
   {
      super(message);
   }

   public DBUnitInitializationException(Throwable cause)
   {
      super(cause);
   }

}
