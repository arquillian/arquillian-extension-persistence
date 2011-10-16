package org.jboss.arquillian.persistence;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.event.TransactionFinished;
import org.jboss.arquillian.persistence.metadata.MetadataProvider;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.Before;

public class TransactionalWrapper
{

   private static final String USER_TRANSACTION_JNDI_NAME = "java:comp/UserTransaction";

   @Inject @SuiteScoped
   private Instance<PersistenceConfiguration> configuration;

   @Inject @TestScoped
   private InstanceProducer<UserTransaction> transactionProducer;
   
   @Inject @TestScoped
   private Instance<UserTransaction> transaction;
   
   public void beforeTest(@Observes(precedence = 10) Before beforeTestEvent) throws Exception
   {
      MetadataProvider metadataProvider = new MetadataProvider(beforeTestEvent, configuration.get());
      if (!metadataProvider.isTransactional())
      {
         return;
      }
      transaction.get().begin();
   }
   
   public void afterTest(@Observes(precedence = 10) TransactionFinished transactionFinished) throws Exception
   {
      MetadataProvider metadataProvider = new MetadataProvider(transactionFinished, configuration.get());

      TransactionMode mode = metadataProvider.getTransactionalMode();
      if (TransactionMode.COMMIT.equals(mode))
      {
         transaction.get().commit();
      }
      else
      {
         transaction.get().rollback();
      }
   }
   
   public void obtainTransaction(@Observes(precedence = 100) Before beforeTestEvent)
   {
      try
      {
         final InitialContext context = new InitialContext();
         UserTransaction userTransaction = (UserTransaction) context.lookup(USER_TRANSACTION_JNDI_NAME);
         transactionProducer.set(userTransaction);
      }
      catch (NamingException e)
      {
         throw new RuntimeException("Failed obtaining transaction.");
      }
   }
   
}
