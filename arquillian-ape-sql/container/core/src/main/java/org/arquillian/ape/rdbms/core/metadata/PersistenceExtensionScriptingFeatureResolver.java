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

import java.lang.reflect.Method;
import org.arquillian.ape.rdbms.CleanupUsingScript;
import org.arquillian.ape.api.TestExecutionPhase;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class PersistenceExtensionScriptingFeatureResolver {

    private final ScriptingConfiguration configuration;

    private final DbUnitMetadataExtractor metadataExtractor;

    private final Method testMethod;

    public PersistenceExtensionScriptingFeatureResolver(Method testMethod, DbUnitMetadataExtractor metadataExtractor,
        ScriptingConfiguration configuration) {
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
