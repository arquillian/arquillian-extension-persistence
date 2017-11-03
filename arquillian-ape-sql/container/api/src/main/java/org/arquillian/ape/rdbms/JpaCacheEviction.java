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
package org.arquillian.ape.rdbms;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import org.arquillian.ape.api.TestExecutionPhase;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that test want evict JPA second level cache between test method
 * invocation.
 * <p>
 * Necessary attribute which must be set in annotation or in configuration is
 * JNDI name(s) of entity manager(s), other attributes have default values.
 * Default phase is before test method invocation and is used strategy which
 * evict all entities from cache. Default values can be changed in Arquillian
 * descriptor.
 * <p>
 * Eviction can be defined on test class or test method level, where latter takes precedence if both are present.
 * <p>
 * Example configuration:
 * <pre>
 * <code>
 * &lt;extension qualifier="persistence-jpacacheeviction"&gt;
 *     &lt;property name="defaultPhase"&gt;AFTER&lt;/property&gt;
 *     &lt;property name="defaultEntityManager"&gt;java:comp/env/MyPersistenceUnit&lt;/property&gt;
 *     &lt;property name="defaultStrategy"&gt;com.mycompany.MyCustomJpaCacheEvictionStrategy&lt;/property&gt;
 * &lt;/extension&gt;
 * </code>
 * </pre>
 * <p>
 * Example test:
 * <pre>
 * <code>
 * &#064;RunWith(Arquillian.class)
 * &#064;JpaCacheEviction(entityManager = "java:comp/env/MyPersistenceUnit")
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
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface JpaCacheEviction {

    /**
     * Defines when during the test execution second level cache should be evicted.
     */
    TestExecutionPhase phase() default TestExecutionPhase.DEFAULT;

    /**
     * Defines JNDI names of entity managers which second level caches should be evicted
     * during test execution. Both full JNDI name as well as short form can be provided.
     * In case of latter it will be prefixed with java:comp/env/.
     */
    String[] entityManager() default "";

    /**
     * Defines strategy how to evict cache.
     */
    Class<? extends JpaCacheEvictionStrategy> strategy() default DefaultJpaCacheEvictionStrategy.class;

    /**
     * Internal strategy class for representing default value.
     *
     * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
     */
    class DefaultJpaCacheEvictionStrategy implements JpaCacheEvictionStrategy {
        /**
         * @see JpaCacheEvictionStrategy#evictCache(javax.persistence.EntityManager)
         */
        @Override
        public void evictCache(EntityManager em) {
            throw new UnsupportedOperationException();
        }
    }
}
