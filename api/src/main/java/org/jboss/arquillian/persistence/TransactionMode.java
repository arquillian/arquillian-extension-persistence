package org.jboss.arquillian.persistence;

public enum TransactionMode 
{
   /**
    * Rollback transaction after every test. 
    */
   ROLLBACK,
   /**
    * Commit transaction after test execution.
    * Default bevahiour.
    */
   COMMIT, 
   /**
    * Instructs extension to not use transactions for tests execution.
    */
   DISABLED;
}
