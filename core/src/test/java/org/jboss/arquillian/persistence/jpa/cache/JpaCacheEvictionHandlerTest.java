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
package org.jboss.arquillian.persistence.jpa.cache;

import net.jcip.annotations.NotThreadSafe;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.persistence.JpaCacheEviction;
import org.jboss.arquillian.persistence.JpaCacheEvictionStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.core.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.core.event.BeforePersistenceTest;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Before;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 */
@NotThreadSafe
public class JpaCacheEvictionHandlerTest
{
   private static boolean tmpCacheEvicted;
   private static String tmpLookupName;
   private JpaCacheEvictionHandler jpaCacheEvictionHandler;
   private JpaCacheEvictionConfiguration jpaCacheEvictionConfiguration;

   @Before
   public void init() throws Exception {
      tmpCacheEvicted = false;
      tmpLookupName = null;

      StubContext stubContext = new StubContext();
      StubContextInstance stubContextInstance = new StubContextInstance(stubContext);

      jpaCacheEvictionConfiguration = new JpaCacheEvictionConfiguration();
      jpaCacheEvictionConfiguration.setDefaultEntityManager("defaultEntityManager");
      jpaCacheEvictionConfiguration.setDefaultStrategy(StubJpaCacheEvictionStrategy.class);

      jpaCacheEvictionHandler = new JpaCacheEvictionHandler(stubContextInstance, jpaCacheEvictionConfiguration);
   }

   @Test
   public void should_do_nothing() throws Exception
   {
      jpaCacheEvictionHandler.onBeforeTestMethod(createBeforeEvent(new StubTestCaseNoAnnotation()));
      assertThat(tmpLookupName).isNull();
      assertThat(tmpCacheEvicted).isFalse();

      jpaCacheEvictionHandler.onAfterTestMethod(createAfterEvent(new StubTestCaseNoAnnotation()));
      assertThat(tmpLookupName).isNull();
      assertThat(tmpCacheEvicted).isFalse();

      jpaCacheEvictionHandler.onAfterTestMethod(createAfterEvent(new StubTestCaseWithAnnotation()));
      assertThat(tmpLookupName).isNull();
      assertThat(tmpCacheEvicted).isFalse();

      jpaCacheEvictionHandler.onAfterTestMethod(createAfterEvent(new StubTestCaseWithInheritedAnnotation()));
      assertThat(tmpLookupName).isNull();
      assertThat(tmpCacheEvicted).isFalse();

      jpaCacheEvictionHandler.onBeforeTestMethod(createBeforeEvent(new StubTestCaseWithModifiedAnnotation()));
      assertThat(tmpLookupName).isNull();
      assertThat(tmpCacheEvicted).isFalse();
   }

   @Test
   public void should_evict_cache_before_test_method() throws Exception
   {
      jpaCacheEvictionHandler.onBeforeTestMethod(createBeforeEvent(new StubTestCaseWithAnnotation()));
      assertThat(tmpLookupName).isEqualTo("defaultEntityManager");
      assertThat(tmpCacheEvicted).isTrue();

      tmpLookupName = null;
      tmpCacheEvicted = false;

      jpaCacheEvictionHandler.onBeforeTestMethod(createBeforeEvent(new StubTestCaseWithInheritedAnnotation()));
      assertThat(tmpLookupName).isEqualTo("defaultEntityManager");
      assertThat(tmpCacheEvicted).isTrue();
   }

   @Test
   public void should_evict_cache_after_test_method() throws Exception
   {
      jpaCacheEvictionHandler.onAfterTestMethod(createAfterEvent(new StubTestCaseWithModifiedAnnotation()));
      assertThat(tmpLookupName).isEqualTo("customEntityManager");
      assertThat(tmpCacheEvicted).isTrue();
   }

   private BeforePersistenceTest createBeforeEvent(Object testInstance) throws Exception
   {
      return new BeforePersistenceTest(new TestEvent(testInstance, StubTestCaseNoAnnotation.class.getDeclaredMethod("stubTestMethod", new Class[]{})));
   }

   private AfterPersistenceTest createAfterEvent(Object testInstance) throws Exception
   {
      return new AfterPersistenceTest(new TestEvent(testInstance, StubTestCaseNoAnnotation.class.getDeclaredMethod("stubTestMethod", new Class[]{})));
   }


   public static class StubContext extends InitialContext
   {

      public StubContext() throws NamingException
      {
         super(true);
      }

      @Override
      public Object lookup(String name) throws NamingException
      {
         tmpLookupName = name;
         return null;
      }

   }

   public static class StubContextInstance implements Instance<Context> {

      private Context instance;

      public StubContextInstance(Context instance)
      {
         this.instance = instance;
      }

      @Override
      public Context get()
      {
         return instance;
      }

   }

   public static class StubJpaCacheEvictionStrategy implements JpaCacheEvictionStrategy
   {
      @Override
      public void evictCache(EntityManager em)
      {
         tmpCacheEvicted = true;
      }
   }

   public static class StubTestCaseNoAnnotation
   {
      public void stubTestMethod() {}
   }

   @JpaCacheEviction
   public static class StubTestCaseWithAnnotation extends StubTestCaseNoAnnotation {}

   @JpaCacheEviction(phase = TestExecutionPhase.AFTER, entityManager = "customEntityManager")
   public static class StubTestCaseWithModifiedAnnotation extends StubTestCaseNoAnnotation {}

   public static class StubTestCaseWithInheritedAnnotation extends StubTestCaseWithAnnotation {}
}
