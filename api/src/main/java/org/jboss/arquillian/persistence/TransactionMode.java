package org.jboss.arquillian.persistence;

public enum TransactionMode 
{
   /**
    * Rollback transaction after every test. 
    * Default bevahiour.
    */
   ROLLBACK,
   /**
    * Commit transaction after test execution.
    */
   COMMIT;
}
