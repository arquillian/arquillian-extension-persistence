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

import org.arquillian.ape.rdbms.ApplyScriptAfter;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupUsingScript;
import org.arquillian.ape.rdbms.CreateSchema;
import org.arquillian.ape.rdbms.DataSource;
import org.arquillian.ape.rdbms.JpaCacheEviction;
import org.arquillian.ape.rdbms.PersistenceTest;
import org.arquillian.ape.rdbms.SeedDataUsing;
import org.arquillian.ape.rdbms.ShouldMatchDataSet;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.arquillian.persistence.*;
import org.jboss.arquillian.test.spi.TestClass;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class MetadataExtractor {

    private final TestClass testClass;

    private final Map<Class<?>, AnnotationInspector<?>> inspectors = new HashMap<Class<?>, AnnotationInspector<?>>();

    public MetadataExtractor(TestClass testClass) {
        this.testClass = testClass;
    }

    public <K extends Annotation> void register(final TestClass testClass, final Class<K> annotation) {
        inspectors.put(annotation, new AnnotationInspector<K>(testClass, annotation));
    }

    @SuppressWarnings("unchecked")
    public <K extends Annotation> AnnotationInspector<K> using(final Class<K> annotation) {
        if (inspectors.get(annotation) == null) {
            register(testClass, annotation);
        }
        return (AnnotationInspector<K>) inspectors.get(annotation);
    }

    public AnnotationInspector<DataSource> dataSource() {
        return using(DataSource.class);
    }

    public AnnotationInspector<SeedDataUsing> dataSeedStrategy() {
        return using(SeedDataUsing.class);
    }

    public AnnotationInspector<UsingDataSet> usingDataSet() {
        return using(UsingDataSet.class);
    }

    public AnnotationInspector<ShouldMatchDataSet> shouldMatchDataSet() {
        return using(ShouldMatchDataSet.class);
    }

    public AnnotationInspector<ApplyScriptBefore> applyScriptBefore() {
        return using(ApplyScriptBefore.class);
    }

    public AnnotationInspector<ApplyScriptAfter> applyScriptAfter() {
        return using(ApplyScriptAfter.class);
    }

    public AnnotationInspector<Cleanup> cleanup() {
        return using(Cleanup.class);
    }

    public AnnotationInspector<CleanupUsingScript> cleanupUsingScript() {
        return using(CleanupUsingScript.class);
    }

    public AnnotationInspector<JpaCacheEviction> jpaCacheEviction() {
        return using(JpaCacheEviction.class);
    }

    public AnnotationInspector<CreateSchema> createSchema() {
        return using(CreateSchema.class);
    }

    public boolean hasPersistenceTestAnnotation() {
        return testClass.getAnnotation(PersistenceTest.class) != null;
    }

    public Class<?> getJavaClass() {
        return testClass.getJavaClass();
    }

}
