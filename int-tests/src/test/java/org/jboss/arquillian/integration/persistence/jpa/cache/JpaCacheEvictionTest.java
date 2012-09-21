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
package org.jboss.arquillian.integration.persistence.jpa.cache;

import static org.fest.assertions.Assertions.assertThat;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.JpaCacheEviction;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 */
@RunWith(Arquillian.class)
@JpaCacheEviction(entityManager = "jpacacheeviction")
@Transactional
public class JpaCacheEvictionTest
{

   @Deployment
   public static WebArchive createDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "jpacacheeviction-test.war")
                       .addClasses(Game.class, Platform.class, GameBean.class)
                       .addAsResource("test-jpacacheeviction-persistence.xml", "META-INF/persistence.xml")
                       .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
   }

   @Inject
   private GameBean gameBean;

   @PersistenceUnit(unitName = "jpacacheeviction")
   private EntityManagerFactory emf;

   @Test
   @InSequence(value = 1)
   public void should_put_game_to_second_level_cache() throws Exception
   {
      // given
      assertThat(isGameEntityCached()).as("Expected: Second level cache was evicted").isFalse();

      // when
      gameBean.init();
      Game game = gameBean.findById(1L);

      // then
      assertThat(game).isNotNull();
      assertThat(isGameEntityCached()).as("Expected: Second level cache contains entity").isTrue();
   }

   @Test
   @InSequence(value = 2)
   public void should_evict_cache_before_test_method()
   {
      assertThat(isGameEntityCached()).as("Expected: Second level cache cache was evicted").isFalse();
   }

   // Private helper methods

   private boolean isGameEntityCached()
   {
      return gameBean.isCached(1L);
   }

}