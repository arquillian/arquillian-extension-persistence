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
package org.arquillian.ape.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jboss.arquillian.test.spi.TestClass;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class AnnotationInspector<T extends Annotation> {

    private final Map<Method, List<T>> annotatedMethods;

    private final TestClass testClass;

    private final Class<T> annotationClass;

    public AnnotationInspector(TestClass testClass, Class<T> annotationClass) {
        this.testClass = testClass;
        this.annotationClass = annotationClass;
        this.annotatedMethods = fetch(annotationClass);
    }

    public Collection<T> fetchAll() {
        final Set<T> all = new HashSet<>();

        final Collection<List<T>> values = annotatedMethods.values();
        values.stream().forEach(all::addAll);

        Collection<T> annotationsOnClassLevel = getAnnotationsOnClassLevel();
        all.addAll(annotationsOnClassLevel);

        return all;
    }

    public boolean isDefinedOn(Method method) {
        return fetchFrom(method) != null;
    }

    public boolean isDefinedOnAnyMethod() {
        return !annotatedMethods.isEmpty();
    }

    public T fetchFrom(Method method) {
        final List<T> allAnnotations = annotatedMethods.get(method);

        if (allAnnotations == null || allAnnotations.size() == 0) {
            return null;
        }

        return allAnnotations.get(0);

    }

    public Collection<T> fetchAllFrom(Method method) {
        return annotatedMethods.get(method);
    }

    public Collection<T> fetchAllFrom(Method method, Predicate<T> predicate) {
        final List<T> annotations = annotatedMethods.get(method);

        if (annotations != null) {
            return annotations.stream().filter(predicate).collect(Collectors.toList());
        }

        return annotations;
    }

    public boolean isDefinedOnClassLevel() {
        return getAnnotationOnClassLevel() != null;
    }

    public T getAnnotationOnClassLevel() {
        return testClass.getAnnotation(annotationClass);
    }

    public Collection<T> getAnnotationsOnClassLevel() {
        return Arrays.asList(testClass.getJavaClass().getAnnotationsByType(annotationClass));
    }

    public Collection<T> getAnnotationsOnClassLevel(Predicate<T> predicate) {
        return Arrays.asList(testClass.getJavaClass().getAnnotationsByType(annotationClass)).stream()
                                                .filter(predicate)
                                                .collect(Collectors.toList());
    }

    /**
     * Fetches annotation for a given test class. If annotation is defined on method
     * level it's returned as a result. Otherwise class level annotation is returned if present.
     *
     * @return T annotation or null if not found.
     */
    public T fetchUsingFirst(Method testMethod) {
        T usedAnnotation = getAnnotationOnClassLevel();
        if (isDefinedOn(testMethod)) {
            usedAnnotation = fetchFrom(testMethod);
        }

        return usedAnnotation;
    }

    public Collection<T> fetchUsingFirst(Method testMethod, Predicate<T> predicate) {
        Collection<T> usedAnnotations = getAnnotationsOnClassLevel(predicate);

        if (isDefinedOn(testMethod)) {
            usedAnnotations = fetchAllFrom(testMethod, predicate);
        }

        return usedAnnotations;

    }

    // Private

    private Map<Method, List<T>> fetch(Class<T> annotation) {
        final Map<Method, List<T>> map = new HashMap<>();

        for (Method testMethod : testClass.getJavaClass().getMethods()) {
            final T[] annotationsByType = testMethod.getAnnotationsByType(annotation);
            if (annotationsByType.length > 0) {
                map.putIfAbsent(testMethod, new ArrayList<>());
                map.get(testMethod).addAll(Arrays.asList(annotationsByType));
            }
        }

        return map;
    }

}

