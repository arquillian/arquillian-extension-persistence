package org.jboss.arquillian.persistence.data.dbunit;

public class DBUnitInitilaztionException extends RuntimeException
{

   private static final long serialVersionUID = -7080488005712280266L;

   public DBUnitInitilaztionException()
   {
      super();
   }

   public DBUnitInitilaztionException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public DBUnitInitilaztionException(String message)
   {
      super(message);
   }

   public DBUnitInitilaztionException(Throwable cause)
   {
      super(cause);
   }

}
