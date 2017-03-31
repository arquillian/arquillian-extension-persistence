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
package org.arquillian.ape.rdbms.core.lifecycle;

import org.arquillian.ape.rdbms.core.event.AfterPersistenceTest;
import org.arquillian.ape.rdbms.core.event.BeforePersistenceTest;
import org.arquillian.ape.rdbms.core.test.AssertionErrorCollector;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

public class ErrorCollectorHandler {

    @Inject
    @TestScoped
    private InstanceProducer<AssertionErrorCollector> assertionErrorCollectorProducer;

    public void createErrorCollector(@Observes(precedence = 10000) EventContext<BeforePersistenceTest> context) {
        assertionErrorCollectorProducer.set(new AssertionErrorCollector());
        context.proceed();
    }

    public void collectErrors(@Observes(precedence = 10000) EventContext<AfterPersistenceTest> context) {
        // It's needed to intercept persistence test event using context
        // since there is also connection closed in DBUnitPersistenceTestLifecycleHandler
        // which needs to take place before reporting any errors.
        context.proceed();
        assertionErrorCollectorProducer.get().report();
    }
}
