/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.example;

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
