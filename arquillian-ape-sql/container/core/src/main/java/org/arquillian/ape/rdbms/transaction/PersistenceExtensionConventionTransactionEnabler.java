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
package org.arquillian.ape.rdbms.transaction;

import java.lang.reflect.Method;
import org.arquillian.ape.rdbms.core.configuration.PersistenceConfiguration;
import org.arquillian.ape.rdbms.core.metadata.MetadataExtractor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.spi.provider.TransactionEnabler;

public class PersistenceExtensionConventionTransactionEnabler implements TransactionEnabler {

    @Inject
    Instance<PersistenceConfiguration> persistenceConfiguration;

    @Inject
    Instance<MetadataExtractor> metadataExtractor;

    @Override
    public boolean isTransactionHandlingDefinedOnClassLevel(TestEvent testEvent) {
        return hasTransactionMetadataDefinedOnClassLevel();
    }

    @Override
    public boolean isTransactionHandlingDefinedOnMethodLevel(TestEvent testEvent) {
        return shouldWrapTestMethodInTransaction(testEvent.getTestMethod());
    }

    @Override
    public TransactionMode getTransactionModeFromClassLevel(TestEvent testEvent) {
        return persistenceConfiguration.get().getDefaultTransactionMode();
    }

    @Override
    public TransactionMode getTransactionModeFromMethodLevel(TestEvent testEvent) {
        return persistenceConfiguration.get().getDefaultTransactionMode();
    }

    // ---------------------------------------------------------------------------------------------------
    // Internal methods
    // ---------------------------------------------------------------------------------------------------

    private boolean shouldWrapTestMethodInTransaction(final Method method) {
        return (hasDataSetAnnotation(method) || hasApplyScriptAnnotation(method)
            || hasJpaCacheEvictionAnnotation(method)
            || hasCleanupAnnotation(method)
            || hasCleanupUsingScriptAnnotation(method));
    }

    private boolean hasTransactionMetadataDefinedOnClassLevel() {
        return (hasDataSetAnnotationOnClass() || hasApplyScriptAnnotationOnClass()
            || hasPersistenceTestAnnotationOnClass() || hasJpaCacheEvictionAnnotationOnClass()
            || hasCreateSchemaAnnotationOnClass() || hasCleanupAnnotationOnClass()
            || hasCleanupUsingScriptAnnotationOnClass());
    }

    private boolean hasDataSetAnnotationOnClass() {
        return metadataExtractor.get().usingDataSet().isDefinedOnClassLevel()
            || metadataExtractor.get().shouldMatchDataSet().isDefinedOnClassLevel();
    }

    private boolean hasDataSetAnnotation(final Method method) {
        return metadataExtractor.get().usingDataSet().isDefinedOn(method)
            || metadataExtractor.get().shouldMatchDataSet().isDefinedOn(method);
    }

    private boolean hasApplyScriptAnnotation(final Method method) {
        return metadataExtractor.get().applyScriptBefore().isDefinedOn(method)
            || metadataExtractor.get().applyScriptAfter().isDefinedOn(method);
    }

    private boolean hasApplyScriptAnnotationOnClass() {
        return metadataExtractor.get().applyScriptBefore().isDefinedOnClassLevel()
            || metadataExtractor.get().applyScriptAfter().isDefinedOnClassLevel();
    }

    private boolean hasPersistenceTestAnnotationOnClass() {
        return metadataExtractor.get().hasPersistenceTestAnnotation();
    }

    private boolean hasJpaCacheEvictionAnnotationOnClass() {
        return metadataExtractor.get().jpaCacheEviction().isDefinedOnClassLevel();
    }

    private boolean hasJpaCacheEvictionAnnotation(final Method method) {
        return metadataExtractor.get().jpaCacheEviction().isDefinedOn(method);
    }

    private boolean hasCreateSchemaAnnotationOnClass() {
        return metadataExtractor.get().createSchema().isDefinedOnClassLevel();
    }

    private boolean hasCleanupAnnotationOnClass() {
        return metadataExtractor.get().cleanup().isDefinedOnClassLevel();
    }

    private boolean hasCleanupAnnotation(final Method method) {
        return metadataExtractor.get().cleanup().isDefinedOn(method);
    }

    private boolean hasCleanupUsingScriptAnnotationOnClass() {
        return metadataExtractor.get().cleanupUsingScript().isDefinedOnClassLevel();
    }

    private boolean hasCleanupUsingScriptAnnotation(final Method method) {
        return metadataExtractor.get().cleanupUsingScript().isDefinedOn(method);
    }
}
