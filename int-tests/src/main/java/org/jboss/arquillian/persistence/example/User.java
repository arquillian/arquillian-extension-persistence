package org.jboss.arquillian.persistence.example;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @author Bartosz Majsak
 *
 */
@Entity
public class User
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Basic
   @NotNull
   @Size(min = 3, max = 20)
   private String username;

   @Basic
   @NotNull
   @Size(min = 8, max = 20)
   private String password;

   @Basic
   @NotNull
   @Size(max = 128)
   private String firstname;

   @Basic
   @NotNull
   @Size(max = 128)
   private String lastname;

   public Long getId()
   {
      return id;
   }

   void setId(Long id)
   {
      this.id = id;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getFirstname()
   {
      return firstname;
   }

   public void setFirstname(String firstname)
   {
      this.firstname = firstname;
   }

   public String getLastname()
   {
      return lastname;
   }

   public void setLastname(String lastname)
   {
      this.lastname = lastname;
   }

}
