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
package org.jboss.arquillian.persistence.configuration;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.context.ApplicationContext;
import org.jboss.arquillian.persistence.client.PersistenceConfigurationProducer;
import org.jboss.arquillian.test.spi.context.SuiteContext;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationInitializationTest extends AbstractTestTestBase {

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(PersistenceConfigurationProducer.class);
    }

    @Before
    public void initializeArquillianDescriptor() {
        bind(ApplicationScoped.class, ArquillianDescriptor.class, Descriptors.create(ArquillianDescriptor.class));
    }

    @Test
    public void shouldCreateConfigurationBeforeClassIsExecuted() throws Exception {
        // given
        getManager().getContext(SuiteContext.class).activate();

        // when
        fire(new BeforeSuite());
        PersistenceConfiguration persistenceConfiguration = getManager().getContext(ApplicationContext.class)
                                                                        .getObjectStore()
                                                                        .get(PersistenceConfiguration.class);

        // then
        assertThat(persistenceConfiguration).isNotNull();
    }

}
