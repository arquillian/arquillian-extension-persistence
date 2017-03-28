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
package org.arquillian.integration.ape.testextension.event;

import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.core.event.PersistenceEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EventHandlingVerifier {

    public final Class<? extends PersistenceEvent> event;
    private boolean triggeredBefore;
    private boolean triggeredAfter;
    private boolean triggered;
    private TestExecutionPhase phase = TestExecutionPhase.NONE;
    private TestExecutionPhase expectedPhase;
    private boolean verificationRequested;

    private EventHandlingVerifier(Class<? extends PersistenceEvent> event) {
        this.event = event;
    }

    public boolean wasCalledAfter() {
        return triggeredAfter;
    }

    public void triggeredAfter() {
        phase = TestExecutionPhase.AFTER;
        this.triggeredAfter = true;
    }

    public boolean alreadyTriggered() {
        return triggered;
    }

    public void called() {
        this.triggered = true;
    }

    public boolean wasTriggeredBefore() {
        return triggeredBefore;
    }

    public void triggeredBefore() {
        phase = TestExecutionPhase.BEFORE;
        this.triggeredBefore = true;
    }

    public void shouldBePerformedBeforeTest() {
        verificationRequested = true;
        expectedPhase = TestExecutionPhase.BEFORE;
    }

    public void shouldBePerformedAfterTest() {
        verificationRequested = true;
        expectedPhase = TestExecutionPhase.AFTER;
    }

    public void shouldNotBePerformed() {
        verificationRequested = true;
        expectedPhase = TestExecutionPhase.NONE;
    }

    public void verifyPhaseWhenEventWasTriggered() {
        if (verificationRequested) {
            assertThat(phase).describedAs("Verifying event test phase of [" + event.getCanonicalName() + "]")
                    .isEqualTo(expectedPhase);
        }
    }

    public static class Builder {
        private Class<? extends Annotation> eventExpectedToBeTriggeredAnnotation;
        private Class<? extends Annotation> eventNotExpectedToBeTriggeredAnnotation;
        private Class<? extends PersistenceEvent> event;

        public static Builder eventVerifier() {
            return new Builder();
        }

        public Builder definedFor(Class<? extends PersistenceEvent> event) {
            this.event = event;
            return this;
        }

        public Builder expectedWhen(Class<? extends Annotation> eventVerificationAnnotationTrigger) {
            this.eventExpectedToBeTriggeredAnnotation = eventVerificationAnnotationTrigger;
            return this;
        }

        public Builder notExpectedWhen(Class<? extends Annotation> eventNotTriggeredAnnotation) {
            this.eventNotExpectedToBeTriggeredAnnotation = eventNotTriggeredAnnotation;
            return this;
        }

        public void registerIfPresent(Map<Class<? extends PersistenceEvent>, EventHandlingVerifier> map, Method method) {
            EventHandlingVerifier verifier = new EventHandlingVerifier(event);
            if (expectedToBeCalled(method)) {
                registerEventTriggedExpectation(verifier, method);
                map.put(event, verifier);
            }

            if (notExpectedToBeCalled(method)) {
                registerEventNotTriggedExpectation(verifier);
                map.put(event, verifier);
            }

        }

        private void registerEventNotTriggedExpectation(EventHandlingVerifier verifier) {
            verifier.shouldNotBePerformed();
        }

        private void registerEventTriggedExpectation(EventHandlingVerifier verifier, Method method) {

            TestExecutionPhase phase = extractPhase(method);
            switch (phase) {
                case AFTER:
                    verifier.shouldBePerformedAfterTest();
                    break;
                case BEFORE:
                    verifier.shouldBePerformedBeforeTest();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported test phase " + phase);
            }

        }

        private TestExecutionPhase extractPhase(Method method) {
            final Annotation annotation = method.getAnnotation(eventExpectedToBeTriggeredAnnotation);
            try {
                return (TestExecutionPhase) annotation.annotationType().getMethod("value").invoke(annotation);
            } catch (Exception e) {
                throw new RuntimeException("Unable to fetch test execution phase for " + annotation, e);
            }

        }

        private boolean expectedToBeCalled(Method method) {
            return method.getAnnotation(eventExpectedToBeTriggeredAnnotation) != null;
        }

        public boolean notExpectedToBeCalled(Method method) {
            return method.getAnnotation(eventNotExpectedToBeTriggeredAnnotation) != null;
        }

    }


}
