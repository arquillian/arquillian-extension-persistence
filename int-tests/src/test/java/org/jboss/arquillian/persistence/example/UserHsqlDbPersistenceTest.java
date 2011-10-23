package org.jboss.arquillian.persistence.example;

import static org.fest.assertions.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.Expected;
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
                       .addPackage(UserAccount.class.getPackage())
                       .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsManifestResource("hsql-test-persistence.xml", "persistence.xml");
   }

   @PersistenceContext
   EntityManager em;

   @Test
   @Data({"datasets/single-user.xml", "datasets/address.yml"})
   public void shouldHaveAddressLinkedToUserAccountUsingMultipleFiles() throws Exception
   {
      // given
      String expectedCity = "Metropolis";
      UserAccount user = em.find(UserAccount.class, 1L);

      // when
      Address address = user.getAddresses().iterator().next();
      
      // then
      assertThat(user.getAddresses()).hasSize(1);
      assertThat(address.getCity()).isEqualTo(expectedCity);
   }
   
   @Test
   @Data("datasets/single-user.xls")
   @DataSource("arq/hsql")
   public void shouldFindUserUsingExcelDatasetAndDataSource() throws Exception
   {
      // given
      String expectedUsername = "doovde";

      // when
      UserAccount user = em.find(UserAccount.class, 1L);

      // then 
      assertThat(user.getUsername()).isEqualTo(expectedUsername);
   }
   
   @Test
   @Data("datasets/single-user.xml")
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
   @Data("datasets/user-with-address.yml")
   public void shouldHaveAddressLinkedToUserAccount() throws Exception
   {
      // given
      String expectedCity = "Metropolis";
      long userAccountId = 1L;

      // when
      UserAccount user = em.find(UserAccount.class, userAccountId);
      Address address = user.getAddresses().iterator().next();
      
      // then
      assertThat(user.getAddresses()).hasSize(1);
      assertThat(address.getCity()).isEqualTo(expectedCity);
   }


   @Test
   @Data("datasets/single-user.xml")
   @Expected({"datasets/single-user.xls", "datasets/expected-address.yml"})
   public void shouldAddAddressToUserAccountAndVerifyUsingMultipleFiles() throws Exception
   {
      // given
      UserAccount user = em.find(UserAccount.class, 1L);
      Address address = new Address("Testing Street", 7, "JavaPolis", 1234); 

      // when
      user.addAddress(address);
      em.merge(user);
      
      // then
      assertThat(user.getAddresses()).hasSize(1);
   }

   // TODO test ignoring columns not specified
   
}
