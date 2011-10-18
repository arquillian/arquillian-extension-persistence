package org.jboss.arquillian.persistence.example;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Address
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
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
