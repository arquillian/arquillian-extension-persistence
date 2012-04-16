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
package org.jboss.arquillian.integration.persistence.example;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Address
{

   @Id
   @GeneratedValue
   private Long id;

   @Basic
   @NotNull @Size(min = 4)
   private String streetName;

   @Basic
   private Integer houseNumber;

   @Basic
   @NotNull
   private String city;

   @Basic
   @NotNull
   private Integer zipCode;

   Address()
   {
      // To satisfy JPA
   }

   public Address(String streetName, Integer houseNumber, String city, Integer zipCode)
   {
      this.streetName = streetName;
      this.houseNumber = houseNumber;
      this.city = city;
      this.zipCode = zipCode;
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

   public String getStreetName()
   {
      return streetName;
   }

   public void setStreetName(String streetName)
   {
      this.streetName = streetName;
   }

   public Integer getHouseNumber()
   {
      return houseNumber;
   }

   public void setHouseNumber(Integer houseNumber)
   {
      this.houseNumber = houseNumber;
   }

   public String getCity()
   {
      return city;
   }

   public void setCity(String city)
   {
      this.city = city;
   }

   public Integer getZipCode()
   {
      return zipCode;
   }

   public void setZipCode(Integer zipCode)
   {
      this.zipCode = zipCode;
   }




}
