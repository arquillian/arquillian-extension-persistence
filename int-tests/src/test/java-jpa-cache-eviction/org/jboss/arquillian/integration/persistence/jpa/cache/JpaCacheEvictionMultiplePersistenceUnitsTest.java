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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.JpaCacheEviction;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 */
@RunWith(Arquillian.class)
@JpaCacheEviction(entityManager = "jpacacheeviction")
public class JpaCacheEvictionMultiplePersistenceUnitsTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "JpaCacheEvictionMultiplePersistenceUnitsTest.war")
                .addClasses(Platform.class, Game.class, GameBeanDoublePersistenceContext.class)
                .addAsResource("test-jpacacheeviction-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private GameBeanDoublePersistenceContext gameBean;

    @PersistenceUnit(unitName = "jpacacheeviction")
    private EntityManagerFactory cacheEviction;

    @PersistenceUnit(unitName = "embedded")
    private EntityManagerFactory embedded;

    @Test
    @InSequence(value = 1)
    public void should_put_games_and_platform_to_second_level_cache() throws Exception {
        // given
        assertThat(isGameEntityCached(cacheEviction, 1L)).as("Expected: Second level cache was evicted").isFalse();

        // when
        gameBean.init();
        Game game = gameBean.findById(1L);
        Platform platform = gameBean.findByIdInEmbedded(1L);

        // then
        assertThat(game).isNotNull();
        assertThat(platform).isNotNull();
        assertThat(isGameEntityCached(cacheEviction, 1L)).as("Expected: Second level cache contains entity").isTrue();
        assertThat(isPlatformEntityCached(embedded, 1L)).as("Expected: Second level cache contains entity").isTrue();
    }

    @Test
    @InSequence(value = 2)
    public void should_evict_cache_before_test_method() {
        assertThat(isGameEntityCached(cacheEviction, 1L)).as("Expected: Second level cache cache was evicted").isFalse();
        assertThat(isPlatformEntityCached(embedded, 1L)).as("Expected: Second level cache cache was not evicted").isTrue();
    }

    @Test
    @InSequence(value = 3)
    @JpaCacheEviction(entityManager = {"embedded", "jpacacheeviction"})
    public void should_evict_both_cache_before_test_method() {
        assertThat(isPlatformEntityCached(embedded, 1L)).as("Expected: Second level cache cache was not evicted").isFalse();
    }

    // Private helper methods

    private boolean isGameEntityCached(EntityManagerFactory emf, Long id) {
        return emf.getCache().contains(Game.class, id);
    }

    private boolean isPlatformEntityCached(EntityManagerFactory emf, Long id) {
        return emf.getCache().contains(Platform.class, id);
    }

}