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
package org.arquillian.ape.rdbms.core.metadata;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.CleanupUsingScript;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.arquillian.ape.rdbms.testutils.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceExtensionFeatureResolverCleanupSettingsTest {

    private ScriptingConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultScriptingConfiguration();

    @Test
    public void should_cleanup_using_script_when_defined_on_method_level() throws Exception {
        // given
        TestEvent testEvent = new TestEvent(new CleanupUsingScriptOnMethodLevelSettings(),
            CleanupUsingScriptOnMethodLevelSettings.class.getMethod("shouldPassWhenCleanupUsingScriptDefined"));
        PersistenceExtensionScriptingFeatureResolver persistenceExtensionFeatureResolver =
            new PersistenceExtensionScriptingFeatureResolver(testEvent.getTestMethod(),
                new MetadataExtractor(testEvent.getTestClass()), defaultConfiguration);

        // when
        boolean shouldCleanupUsingScriptAfter = persistenceExtensionFeatureResolver.shouldCleanupUsingScriptAfter();

        // then
        assertThat(shouldCleanupUsingScriptAfter).isTrue();
    }

    // ----------------------------------------------------------------------------------------
    // Classes used for tests

    @Cleanup(phase = TestExecutionPhase.AFTER)
    private static class ClassLevelCleanupAfterSettings {
        public void shouldPass() {
        }
    }

    private static class DefaultCleanupSettings {
        public void shouldPass() {
        }
    }

    @Cleanup
    private static class CleanupUsingScriptOnMethodLevelSettings {
        @CleanupUsingScript(value = "clean.sql", phase = TestExecutionPhase.AFTER)
        public void shouldPassWhenCleanupUsingScriptDefined() {
        }
    }

    private static class MethodLevelCleanupSettings {
        @Cleanup(phase = TestExecutionPhase.AFTER)
        public void shouldPassCleanupAndAfterPhaseDefined() {
        }

        @Cleanup(strategy = CleanupStrategy.USED_ROWS_ONLY)
        public void shouldPassStrategyOnly() {
        }

        @Cleanup(phase = TestExecutionPhase.NONE)
        public void shouldPassPhaseOnly() {
        }

        public void shouldPassUsingDefaults() {
        }
    }
}
