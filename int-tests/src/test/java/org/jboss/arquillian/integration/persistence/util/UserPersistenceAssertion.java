package org.jboss.arquillian.integration.persistence.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.arquillian.integration.persistence.example.UserAccount;

public class UserPersistenceAssertion
{

   private final EntityManager em;

   public UserPersistenceAssertion(EntityManager em)
   {
      this.em = em;
      this.em.clear();
   }

   public void assertUserAccountsStored()
   {
      @SuppressWarnings("unchecked")
      List<UserAccount> savedUserAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();
      assertThat(savedUserAccounts).isNotEmpty();
   }

   public void assertNoUserAccountsStored()
   {
      @SuppressWarnings("unchecked")
      List<UserAccount> savedUserAccounts = em.createQuery(Query.selectAllInJPQL(UserAccount.class)).getResultList();
      assertThat(savedUserAccounts).isEmpty();
   }

}
