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
package org.jboss.arquillian.integration.persistence.jpacacheeviction;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.JpaCacheEviction;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 */
@RunWith(Arquillian.class)
@JpaCacheEviction(entityManagerFactory = "java:comp/env/jpacacheeviction")
public class JpaCacheEvictionIntegrationTest
{

   @Deployment
   public static WebArchive createDeployment()
   {
      WebArchive archive = ShrinkWrap
            .create(WebArchive.class, "jpacacheeviction-test.war")
            .addPackage(Game.class.getPackage())
            .addAsResource("test-jpacacheeviction-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return archive;
   }

   @Inject
   private GameBean gameBean;
   @PersistenceUnit
   private EntityManagerFactory emf;

   @Test
   @InSequence(value = 1)
   public void should_put_game_to_second_level_cache() throws Exception
   {
      assertFalse("Expected: 2lv cache is empty", isGameCached(1L));

      gameBean.init();
      gameBean.findById(1L);

      assertTrue("Expected: 2lv cache contains entity", isGameCached(1L));
   }

   @Test
   @InSequence(value = 2)
   public void should_evict_cache_before_test_method()
   {
      assertFalse("Expected: 2lv cache was evicted", isGameCached(1L));
   }

   private boolean isGameCached(long gameId)
   {
      return emf.getCache().contains(Game.class, gameId);
   }

}