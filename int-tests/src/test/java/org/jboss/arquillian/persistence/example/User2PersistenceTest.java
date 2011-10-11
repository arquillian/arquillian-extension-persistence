package org.jboss.arquillian.persistence.example;

import static org.fest.assertions.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Data;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@DataSource("arq/derby")
public class User2PersistenceTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar").addClass(User.class)
            .addAsManifestResource("derby-test-persistence.xml", "persistence.xml");
      //                        .addAsResource("-test-persistence.xml", "META-INF/persistence.xml");
   }

// missing @PersistenceContext results with non table creation
   
   @PersistenceContext(unitName = "arquillian-derby")
   EntityManager em;
   
   @Data("datasets/single-user.xml")
   public void shouldFetchUserUsingClassLevelDataSource() throws Exception
   {
      // given
      String expecteUsername = "doovde";

      // when
      User2 user = em.find(User2.class, 1L);

      // then 
      assertThat(user.getUsername()).isEqualTo(expecteUsername);
   }

}
