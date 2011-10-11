package org.jboss.arquillian.persistence;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.metadata.MetadataProvider;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;

public class TransactionalWrapper
{

   @Inject @SuiteScoped
   private Instance<PersistenceConfiguration> configuration;

   @Inject @TestScoped
   private InstanceProducer<UserTransaction> transactionProducer;
   
   @Inject @TestScoped
   private Instance<UserTransaction> transaction;
   
   private MetadataProvider metadataProvider;
   
   public void beforeTest(@Observes(precedence = 10) Before beforeTestEvent) throws NotSupportedException, SystemException
   {
      metadataProvider = new MetadataProvider(beforeTestEvent, configuration.get());
      if (!metadataProvider.isTransactional())
      {
         return;
      }
      transaction.get().begin();
   }
   
   public void afterTest(@Observes(precedence = 10) After AfterTestEvent) throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException
   {
      if (!metadataProvider.isTransactional())
      {
         return;
      }

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
      System.out.println("Obtaining transaction...");
      try
      {
         final InitialContext context = new InitialContext();
         UserTransaction userTransaction = (UserTransaction) context.lookup("java:comp/UserTransaction");
         transactionProducer.set(userTransaction);
      }
      catch (NamingException e)
      {
         throw new RuntimeException("Failed obtaining transaction.");
      }
   }
   
}
