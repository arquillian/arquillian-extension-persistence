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
package org.jboss.arquillian.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.Cache;
import javax.persistence.EntityManagerFactory;

/**
 * Indicates that test want evict JPA second level cache between test method
 * invocation.
 * <p>
 * Necessary attribute which must be set in annotation or in configuration is
 * JNDI name of entity manager factory, other attributes have default values.
 * Default phase is before test method invocation and is used strategy which
 * evict all entities from cache. Default values can be changed in arquillian
 * descriptor.
 * <p>
 * Example configuration:
 * <pre>
 * <code>
 * &lt;extension qualifier="persistence-jpacacheeviction"&gt;
 *     &lt;property name="defaultPhase"&gt;AFTER&lt;/property&gt;
 *     &lt;property name="defaultEntityManagerFactory"&gt;java:comp/env/MyPersistenceUnit&lt;/property&gt;
 *     &lt;property name="defaultStrategy"&gt;com.mycompany.MyCustomJpaCacheEvictionStrategy&lt;/property&gt;
 * &lt;/extension&gt;
 * </code>
 * </pre>
 * <p>
 * Example test:
 * <pre>
 * <code>
 * &#064;RunWith(Arquillian.class)
 * &#064;JpaCacheEviction(entityManagerFactory = "java:comp/env/MyPersistenceUnit")
 * public class MyIntegrationTest {
 * ...
 * </code>
 * </pre> 
 * 
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 * @see Cache
 */
@Inherited
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface JpaCacheEviction
{

   /**
    * Defines when will be cache evicted.
    */
   TestExecutionPhase phase() default TestExecutionPhase.DEFAULT;

   /**
    * Defines JNDI name of entity manager factory.
    */
   String entityManagerFactory() default "";

   /**
    * Defines strategy how to evict cache. 
    */
   Class<? extends JpaCacheEvictionStrategy> strategy() default VoidJpaCacheEvictionStrategy.class;

   /**
    * Internal strategy class for representing default value.
    * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
    */
   class VoidJpaCacheEvictionStrategy implements JpaCacheEvictionStrategy
   {
      /**
       * @see org.jboss.arquillian.persistence.JpaCacheEvictionStrategy#evictCache(javax.persistence.EntityManagerFactory)
       */
      @Override
      public void evictCache(EntityManagerFactory emf)
      {
         throw new UnsupportedOperationException();
      }
   }

}