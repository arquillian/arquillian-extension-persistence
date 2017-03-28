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
import org.arquillian.ape.rdbms.core.container.RemotePersistenceExtension;
import org.arquillian.ape.rdbms.core.event.AfterPersistenceTest;
import org.arquillian.ape.rdbms.core.event.BeforePersistenceTest;
import org.arquillian.ape.rdbms.core.event.InitializeConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.LinkedList;

/**
 * JPA cache eviction handler, which is registered in {@link RemotePersistenceExtension}.
 *
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 * @see JpaCacheEviction
 */
public class JpaCacheEvictionHandler {

    private static final String DEFAULT_JNDI_PREFIX = "java:comp/env/";
    @Inject
    private Instance<Context> ctx;
    private JpaCacheEvictionConfiguration jpaCacheEvictionConfiguration;

    public JpaCacheEvictionHandler() {
    }

    public JpaCacheEvictionHandler(Instance<Context> ctx, JpaCacheEvictionConfiguration jpaCacheEvictionConfiguration) {
        this.ctx = ctx;
        this.jpaCacheEvictionConfiguration = jpaCacheEvictionConfiguration;
    }

    public final void initalizeCacheConfiguration(@Observes InitializeConfiguration event) {
        jpaCacheEvictionConfiguration = new JpaCacheEvictionConfiguration();
        Configuration.importTo(jpaCacheEvictionConfiguration).loadFromPropertyFile(jpaCacheEvictionConfiguration.getPrefix() + "properties");
    }

    public final void onBeforeTestMethod(@Observes(precedence = 15) BeforePersistenceTest event) {
        executeCacheEviction(event, TestExecutionPhase.BEFORE);
    }

    public final void onAfterTestMethod(@Observes(precedence = 45) AfterPersistenceTest event) {
        executeCacheEviction(event, TestExecutionPhase.AFTER);
    }

    private void executeCacheEviction(TestEvent event, TestExecutionPhase currentPhase) {
        JpaCacheEviction jpaCacheEviction = obtainAnnotation(event);
        if (jpaCacheEviction != null) {
            final TestExecutionPhase phase = obtainPhase(jpaCacheEviction);
            if (phase.equals(currentPhase)) {
                final Collection<EntityManager> ems = obtainEntityManagers(jpaCacheEviction);
                final JpaCacheEvictionStrategy strategy = obtainStrategy(jpaCacheEviction);
                for (EntityManager em : ems) {
                    strategy.evictCache(em);
                }
            }
        }
    }

    private Collection<EntityManager> obtainEntityManagers(JpaCacheEviction jpaCacheEviction) {
        final Collection<EntityManager> entityManagers = new LinkedList<EntityManager>();
        final String[] emJndiNames = jpaCacheEviction.entityManager();
        for (String emJndiName : emJndiNames) {
            entityManagers.add(obtainEntityManager(emJndiName));
        }
        return entityManagers;
    }

    private JpaCacheEviction obtainAnnotation(TestEvent event) {
        final JpaCacheEviction classLevel = event.getTestClass().getAnnotation(JpaCacheEviction.class);
        final JpaCacheEviction methodLevel = event.getTestMethod().getAnnotation(JpaCacheEviction.class);
        if (methodLevel != null) {
            return methodLevel;
        }
        return classLevel;
    }

    private TestExecutionPhase obtainPhase(JpaCacheEviction jpaCacheEviction) {
        TestExecutionPhase phase = jpaCacheEviction.phase();
        if (TestExecutionPhase.DEFAULT.equals(phase)) {
            phase = jpaCacheEvictionConfiguration.getDefaultPhase();
        }
        return phase;
    }

    private JpaCacheEvictionStrategy obtainStrategy(JpaCacheEviction jpaCacheEviction) {
        Class<? extends JpaCacheEvictionStrategy> strategyClass = jpaCacheEviction.strategy();
        if (JpaCacheEviction.DefaultJpaCacheEvictionStrategy.class.equals(strategyClass)) {
            strategyClass = jpaCacheEvictionConfiguration.getDefaultStrategy();
        }
        try {
            return strategyClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain JpaCacheEvictionStrategy.", e);
        }
    }

    private EntityManager obtainEntityManager(String emJndiName) {
        if (emJndiName.length() == 0) {
            emJndiName = jpaCacheEvictionConfiguration.getDefaultEntityManager();
        }
        try {
            return lookup(emJndiName);
        } catch (NamingException e) {
            try {
                return lookup(DEFAULT_JNDI_PREFIX + emJndiName);
            } catch (NamingException ne) {
                throw new RuntimeException("Failed to obtain EntityManager using JNDI name " + emJndiName + ", but also appending it with default prefix " + DEFAULT_JNDI_PREFIX, e);
            }

        }
    }

    public EntityManager lookup(String emJndiName) throws NamingException {
        return (EntityManager) ctx.get().lookup(emJndiName);
    }

}