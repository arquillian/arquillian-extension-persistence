package org.jboss.arquillian.persistence.example;

import static org.fest.assertions.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Expected;
import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UserHsqlDbPersistenceTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
                       .addClass(UserAccount.class)
                       .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsManifestResource("hsql-test-persistence.xml", "persistence.xml");
   }

   @PersistenceContext
   EntityManager em;
   
   @Test
   @Data("datasets/single-user.xls")
   @DataSource("arq/hsql")
   public void shouldFindUserUsingXmlDatasetAndDataSource() throws Exception
   {
      // given
      String expectedUsername = "doovde";

      // when
      UserAccount user = em.find(UserAccount.class, 1L);

      // then 
      assertThat(user.getUsername()).isEqualTo(expectedUsername);
   }
   
   @Test
   @Data("datasets/users.yml")
   @DataSource("arq/hsql")
   public void shouldFindUserUsingYamlDatasetAndDataSource() throws Exception
   {
      // given
      String expectedUsername = "superman";

      // when
      UserAccount user = em.find(UserAccount.class, 2L);

      // then 
      assertThat(user.getUsername()).isEqualTo(expectedUsername);
   }
   
   @Test
   @Transactional
   @Data("datasets/users.yml")
   @Expected("datasets/expected-users.yml")
   public void shouldChangeUserPassword() throws Exception
   {
      // given
      String expectedPassword = "LexLuthor";
      UserAccount user = em.find(UserAccount.class, 2L);

      // when
      user.setPassword("LexLuthor");
      em.merge(user);
      
      // then 
      assertThat(user.getPassword()).isEqualTo(expectedPassword);
   }
   
   
   @Test
   public void shouldHaveNewPasswordPersisted() throws Exception
   {
      // given
      String expectedPassword = "LexLuthor";

      // when
      UserAccount user = em.find(UserAccount.class, 2L);

      // then 
      assertThat(user.getPassword()).isEqualTo(expectedPassword);
   }

}
