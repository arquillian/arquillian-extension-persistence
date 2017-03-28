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
package org.arquillian.persistence.core.metadata;

import org.arquillian.persistence.CleanupUsingScript;
import org.arquillian.persistence.TestExecutionPhase;
import org.arquillian.persistence.script.configuration.ScriptingConfiguration;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class PersistenceExtensionScriptingFeatureResolver {

    private final ScriptingConfiguration configuration;

    private final MetadataExtractor metadataExtractor;

    private final Method testMethod;

    public PersistenceExtensionScriptingFeatureResolver(Method testMethod, MetadataExtractor metadataExtractor, ScriptingConfiguration configuration) {
        this.metadataExtractor = metadataExtractor;
        this.configuration = configuration;
        this.testMethod = testMethod;
    }

    // ---------------------------------------------------------------------------------------------------
    // Public API methods
    // ---------------------------------------------------------------------------------------------------

    public TestExecutionPhase getCleanupUsingScriptTestPhase() {
        final CleanupUsingScript cleanupAnnotation = metadataExtractor.cleanupUsingScript().fetchUsingFirst(testMethod);

        TestExecutionPhase phase = configuration.getDefaultCleanupUsingScriptPhase();
        if (cleanupAnnotation != null && !TestExecutionPhase.DEFAULT.equals(cleanupAnnotation.phase())) {
            phase = cleanupAnnotation.phase();
        }

        return phase;
    }

    public boolean shouldCleanupUsingScript() {
        return metadataExtractor.cleanupUsingScript().fetchUsingFirst(testMethod) != null;
    }

    public boolean shouldCleanupUsingScriptBefore() {
        return shouldCleanupUsingScript() && TestExecutionPhase.BEFORE.equals(getCleanupUsingScriptTestPhase());
    }

    public boolean shouldCleanupUsingScriptAfter() {
        return shouldCleanupUsingScript() && TestExecutionPhase.AFTER.equals(getCleanupUsingScriptTestPhase());
    }

}
