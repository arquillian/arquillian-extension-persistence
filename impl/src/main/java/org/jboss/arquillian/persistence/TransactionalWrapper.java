package org.jboss.arquillian.persistence;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.event.TransactionFinished;
import org.jboss.arquillian.persistence.event.TransactionStarted;
import org.jboss.arquillian.persistence.metadata.MetadataProvider;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;

public class TransactionalWrapper
{

   private static final String USER_TRANSACTION_JNDI_NAME = "java:comp/UserTransaction";

   @Inject @SuiteScoped
   private Instance<PersistenceConfiguration> configuration;

   private UserTransaction obtainTransaction()
   {
      try
      {
         final InitialContext context = new InitialContext();
         return (UserTransaction) context.lookup(USER_TRANSACTION_JNDI_NAME);
      }
      catch (NamingException e)
      {
         throw new RuntimeException("Failed obtaining transaction.");
      }
   }
   
   public void beforeTest(@Observes TransactionStarted transactionStarted) throws Exception
   {
      MetadataProvider metadataProvider = new MetadataProvider(transactionStarted, configuration.get());
      if (!metadataProvider.isTransactional())
      {
         return;
      }
      obtainTransaction().begin();
   }
   
   public void afterTest(@Observes TransactionFinished transactionFinished) throws Exception
   {
      MetadataProvider metadataProvider = new MetadataProvider(transactionFinished, configuration.get());

      TransactionMode mode = metadataProvider.getTransactionalMode();
      if (TransactionMode.COMMIT.equals(mode))
      {
         obtainTransaction().commit();
      }
      else
      {
         obtainTransaction().rollback();
      }
   }
   
   
}
