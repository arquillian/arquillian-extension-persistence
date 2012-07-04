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
package org.jboss.arquillian.persistence.jpacacheeviction;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.api.event.ManagerStarted;
import org.jboss.arquillian.persistence.JpaCacheEviction;
import org.jboss.arquillian.persistence.JpaCacheEviction.VoidJpaCacheEvictionStrategy;
import org.jboss.arquillian.persistence.JpaCacheEvictionStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.core.configuration.Configuration;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.TestLifecycleEvent;

/**
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 */
public class JpaCacheEvictionHandler {
	
	@Inject
	private Instance<Context> ctx;
	
	private JpaCacheEvictionConfiguration jpaCacheEvictionConfiguration;
	
	public JpaCacheEvictionHandler()
   {
   }
	
   public JpaCacheEvictionHandler(Instance<Context> ctx, JpaCacheEvictionConfiguration jpaCacheEvictionConfiguration)
   {
      this.ctx = ctx;
      this.jpaCacheEvictionConfiguration = jpaCacheEvictionConfiguration;
   }

   public final void onManagerStarted(@Observes ManagerStarted event)
   {
      initConfiguration();
   }
	
   public final void onBeforeTestMethod(@Observes Before event)
   {
      executeCacheEviction(event, TestExecutionPhase.BEFORE);
   }

   public final void onAfterTestMethod(@Observes After event)
   {
      executeCacheEviction(event, TestExecutionPhase.AFTER);
   }
	
   private void executeCacheEviction(TestLifecycleEvent event, TestExecutionPhase currentPhase)
   {
      JpaCacheEviction jpaCacheEviction = obtainAnnotation(event);
      if (jpaCacheEviction != null)
      {
         TestExecutionPhase phase = obtainPhase(jpaCacheEviction);
         if (phase == currentPhase)
         {
            EntityManagerFactory emf = obtainEntityManagerFactory(jpaCacheEviction);
            JpaCacheEvictionStrategy strategy = obtainStrategy(jpaCacheEviction);
            strategy.evictCache(emf);
         }
      }
   }

   private JpaCacheEviction obtainAnnotation(TestLifecycleEvent event)
   {
      return event.getTestClass().getAnnotation(JpaCacheEviction.class);
   }

   private TestExecutionPhase obtainPhase(JpaCacheEviction jpaCacheEviction)
   {
      TestExecutionPhase phase = jpaCacheEviction.phase();
      if (phase == TestExecutionPhase.DEFAULT)
      {
          phase = jpaCacheEvictionConfiguration.getDefaultPhase();
      }
      return phase;
   }

   private JpaCacheEvictionStrategy obtainStrategy(JpaCacheEviction jpaCacheEviction)
   {
      Class<? extends JpaCacheEvictionStrategy> strategyClass = jpaCacheEviction.strategy();
      if (strategyClass == VoidJpaCacheEvictionStrategy.class)
      {
         strategyClass = jpaCacheEvictionConfiguration.getDefaultStrategy();
      }
      try
      {
         return strategyClass.newInstance();
      } 
      catch (Exception e)
      {
         throw new RuntimeException("Failed to obtain JpaCacheEvictionStrategy.", e);
      }
   }

	private EntityManagerFactory obtainEntityManagerFactory(JpaCacheEviction jpaCacheEviction) {
      String emfJndiName = jpaCacheEviction.entityManagerFactory();
      if (emfJndiName.length() == 0)
      {
         emfJndiName = jpaCacheEvictionConfiguration.getDefaultEntityManagerFactory();
      }
      try
      {
         return (EntityManagerFactory) ctx.get().lookup(emfJndiName);
      }
      catch (NamingException e)
      {
         throw new RuntimeException("Failed to obtain EntityManagerFactory.", e);
      }
	}

   private void initConfiguration()
   {
      jpaCacheEvictionConfiguration = new JpaCacheEvictionConfiguration();
      Configuration.importTo(jpaCacheEvictionConfiguration).loadFromPropertyFile(jpaCacheEvictionConfiguration.getPrefix() + "properties");
   }

}