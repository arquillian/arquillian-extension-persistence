package org.jboss.arquillian.persistence.example;

import static org.fest.assertions.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@DataSource("arq/derby")
public class UserDerbyPersistenceTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
                       .addClass(UserAccount.class)
                       .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsManifestResource("derby-test-persistence.xml", "persistence.xml");
   }

   @PersistenceContext(unitName = "arquillian-derby")
   EntityManager em;
   
   @Test
   @Data("datasets/single-user.xml")
   @Transactional(TransactionMode.DISABLED) // TODO Derby throws timeout exception while obtaining lock
   public void shouldFetchUserUsingClassLevelDataSource() throws Exception
   {
      // given
      String expecteUsername = "doovde";

      // when
      UserAccount user = em.find(UserAccount.class, 1L);

      // then 
      assertThat(user.getUsername()).isEqualTo(expecteUsername);
   }

}
