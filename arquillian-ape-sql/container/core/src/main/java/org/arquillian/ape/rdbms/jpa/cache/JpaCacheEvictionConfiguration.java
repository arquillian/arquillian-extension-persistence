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
package org.arquillian.ape.rdbms.jpa.cache;

import org.arquillian.ape.rdbms.JpaCacheEviction;
import org.arquillian.ape.rdbms.JpaCacheEvictionStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.core.configuration.Configuration;

/**
 * JPA cache eviction configuration which can be customized in
 * <code>arquillian.xml</code> descriptor in the element with qualifier
 * <code>persistence-jpacacheeviction</code>.
 *
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 * @see JpaCacheEviction
 */
public class JpaCacheEvictionConfiguration extends Configuration {

    private static final long serialVersionUID = 1L;

    private TestExecutionPhase defaultPhase = TestExecutionPhase.BEFORE;

    private String defaultEntityManager;

    private Class<? extends JpaCacheEvictionStrategy> defaultStrategy = FullCacheEvictionStrategy.class;

    public JpaCacheEvictionConfiguration() {
        super("persistence-jpacacheeviction",
                "arquillian.extension.persistence.jpacacheeviction.");
    }

    public TestExecutionPhase getDefaultPhase() {
        return defaultPhase;
    }

    /**
     * @param defaultPhase Default cache eviction phase.
     */
    public void setDefaultPhase(TestExecutionPhase defaultPhase) {
        this.defaultPhase = defaultPhase;
    }

    public String getDefaultEntityManager() {
        return defaultEntityManager;
    }

    /**
     * @param defaultEntityManager Default JNDI name of entity manager.
     */
    public void setDefaultEntityManager(String defaultEntityManager) {
        this.defaultEntityManager = defaultEntityManager;
    }

    public Class<? extends JpaCacheEvictionStrategy> getDefaultStrategy() {
        return defaultStrategy;
    }

    /**
     * @param defaultStrategy Strategy how to evict cache.
     */
    public void setDefaultStrategy(Class<? extends JpaCacheEvictionStrategy> defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

}