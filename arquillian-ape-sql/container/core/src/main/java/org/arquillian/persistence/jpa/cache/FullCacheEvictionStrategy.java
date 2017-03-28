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
package org.arquillian.persistence.jpa.cache;

import org.arquillian.persistence.JpaCacheEviction;
import org.arquillian.persistence.JpaCacheEvictionStrategy;

import javax.persistence.Cache;
import javax.persistence.EntityManager;

/**
 * Default implementation of {@link JpaCacheEvictionStrategy}, which evict all entities.
 *
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 * @see JpaCacheEviction
 */
public class FullCacheEvictionStrategy implements JpaCacheEvictionStrategy {

    /**
     * @see org.arquillian.persistence.JpaCacheEvictionStrategy#evictCache(javax.persistence.EntityManagerFactory)
     */
    @Override
    public final void evictCache(EntityManager em) {
        final Cache cache = em.getEntityManagerFactory().getCache();
        if (cache != null) {
            cache.evictAll();
        }
    }

}