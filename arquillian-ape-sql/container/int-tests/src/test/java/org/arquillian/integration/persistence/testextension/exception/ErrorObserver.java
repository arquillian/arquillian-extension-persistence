/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.integration.persistence.testextension.exception;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.arquillian.persistence.core.event.AfterPersistenceTest;
import org.arquillian.persistence.core.test.AssertionErrorCollector;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorObserver {
    @Inject
    @TestScoped
    private Instance<AssertionErrorCollector> assertionErrorCollectorInstance;

    public void collectErrors(@Observes(precedence = 9999) EventContext<AfterPersistenceTest> context) {
        context.proceed();
        final ShouldFailWith shouldFailWith = context.getEvent().getTestMethod().getAnnotation(ShouldFailWith.class);
        if (shouldFailWith != null) {
            final AssertionErrorCollector errorCollector = assertionErrorCollectorInstance.get();
            final Class<? extends Throwable> expectedError = shouldFailWith.value();
            assertThat(errorCollector.contains(expectedError)).describedAs("Expected " + expectedError.getName() + ", but instead got following errors: " + errorCollector.showAllErrors()).isTrue();
            errorCollector.clear();
        }
    }
}
