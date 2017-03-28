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
package org.arquillian.integration.ape.jpa.cache;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 */
@Stateless
public class GameBean {

    @PersistenceContext(name = "jpacacheeviction", unitName = "jpacacheeviction")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void init() {
        em.createNativeQuery("insert into Game(id, title) values (1, 'Pac Man')").executeUpdate();
        em.createNativeQuery("insert into Game(id, title) values (2, 'Super Mario')").executeUpdate();
        em.createNativeQuery("insert into Game(id, title) values (3, 'Sonic')").executeUpdate();
    }

    public Game findById(long gameId) {
        return em.find(Game.class, gameId);
    }

    public boolean isCached(long id) {
        return em.getEntityManagerFactory().getCache().contains(Game.class, id);
    }

}