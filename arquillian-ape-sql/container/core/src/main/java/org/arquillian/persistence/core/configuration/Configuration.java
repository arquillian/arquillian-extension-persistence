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
package org.arquillian.persistence.core.configuration;

import java.io.Serializable;

public abstract class Configuration implements Serializable {

    private static final long serialVersionUID = 4526260174139911102L;

    private final String qualifier;

    private final String prefix;

    public Configuration(String qualifier, String prefix) {
        this.qualifier = qualifier;
        this.prefix = prefix;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getPrefix() {
        return prefix;
    }

    public static <K extends Configuration> ConfigurationImporter<K> importTo(K configuration) {
        return new ConfigurationImporter<K>(configuration);
    }

    public static <K extends Configuration> ConfigurationExporter<K> exportUsing(K configuration) {
        return new ConfigurationExporter<K>(configuration);
    }

    @Override
    public String toString() {
        return super.toString() + "[qualifier=" + qualifier + "]";
    }

}
