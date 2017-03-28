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

import org.arquillian.ape.rdbms.core.event.AfterPersistenceTest;
import org.arquillian.ape.rdbms.core.event.BeforePersistenceTest;
import org.arquillian.ape.rdbms.core.event.CleanupData;
import org.arquillian.ape.rdbms.core.event.CleanupDataUsingScript;
import org.arquillian.ape.rdbms.core.event.ExecuteScripts;
import org.arquillian.ape.rdbms.core.event.PersistenceEvent;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.arquillian.integration.ape.testextension.event.annotation.*;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;

import java.util.HashMap;
import java.util.Map;

import static org.arquillian.integration.ape.testextension.event.EventHandlingVerifier.Builder.eventVerifier;

public class EventObserver {
    @Inject
    @TestScoped
    InstanceProducer<Map<Class<? extends PersistenceEvent>, EventHandlingVerifier>> eventTriggersInstance;

    public void create(@Observes(precedence = 1000000) Before before) {
        eventTriggersInstance.set(new HashMap<Class<? extends PersistenceEvent>, EventHandlingVerifier>());

        eventVerifier()
                .definedFor(CleanupData.class)
                .expectedWhen(CleanupShouldBeTriggered.class)
                .notExpectedWhen(CleanupShouldNotBeTriggered.class)
                .registerIfPresent(eventTriggersInstance.get(), before.getTestMethod());

        eventVerifier()
                .definedFor(CleanupDataUsingScript.class)
                .expectedWhen(CleanupUsingScriptShouldBeTriggered.class)
                .notExpectedWhen(CleanupUsingScriptShouldNotBeTriggered.class)
                .registerIfPresent(eventTriggersInstance.get(), before.getTestMethod());

        eventVerifier()
                .definedFor(ExecuteScripts.class)
                .expectedWhen(ExecuteScriptsShouldBeTriggered.class)
                .notExpectedWhen(ExecuteScriptsShouldNotBeTriggered.class)
                .registerIfPresent(eventTriggersInstance.get(), before.getTestMethod());
    }

    public void observeCalls(@Observes PersistenceEvent persistenceEvent) {
        if (eventTriggersInstance.get() == null) {
            return;
        }
        EventHandlingVerifier verifier = eventTriggersInstance.get().get(persistenceEvent.getClass());
        if (verifier != null) {
            verifier.called();
        }
    }

    public void verify(@Observes(precedence = -1000000) After after) {
        for (EventHandlingVerifier eventVerifier : eventTriggersInstance.get().values()) {
            eventVerifier.verifyPhaseWhenEventWasTriggered();
        }
    }


    public void beforeTest(@Observes(precedence = -100000) BeforePersistenceTest beforePersistenceTest) {
        for (EventHandlingVerifier eventVerifier : eventTriggersInstance.get().values()) {
            if (eventVerifier.alreadyTriggered()) {
                eventVerifier.triggeredBefore();
            }
        }
    }

    public void afterTest(@Observes(precedence = -100000) AfterPersistenceTest afterPersistenceTest) {
        for (EventHandlingVerifier eventVerifier : eventTriggersInstance.get().values()) {
            if (eventVerifier.alreadyTriggered() && !eventVerifier.wasTriggeredBefore()) {
                eventVerifier.triggeredAfter();
            }
        }
    }

}
