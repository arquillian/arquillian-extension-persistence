package org.jboss.arquillian.persistence.example;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @author Bartosz Majsak
 *
 */
@Entity
public class UserAccount
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Basic
   @NotNull @Size(min = 3, max = 32)
   private String username;

   @Basic
   @NotNull @Size(min = 8)
   private String password;

   @Basic
   @NotNull @Size(max = 128)
   private String firstname;

   @Basic
   @NotNull @Size(max = 128)
   private String lastname;
   
   @OneToMany
   private Set<Address> addresses = new HashSet<Address>();

   UserAccount()
   {
      // To satisfy JPA
   }
   
   public void addAddress(Address address)
   {
      this.addresses.add(address);
   }
   
   // Getters and setters
   
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

   public Set<Address> getAddresses()
   {
      return Collections.unmodifiableSet(addresses);
   }

   void setAddresses(Set<Address> addresses)
   {
      this.addresses = addresses;
   }
   
   

}
