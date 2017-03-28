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
package org.arquillian.persistence;

/**
 * Defines strategy to be applied for {@link @Cleanup} operation.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public enum CleanupStrategy {
    /**
     * Cleans entire database.
     * Might require turning off database constraints (e.g. referential integrity).
     */
    STRICT {
        @Override
        public <T> T provide(StrategyProvider<T> provider) {
            return provider.strictStrategy();
        }
    },

    /**
     * Deletes only those entries which were defined in data sets.
     */
    USED_ROWS_ONLY {
        @Override
        public <T> T provide(StrategyProvider<T> provider) {
            return provider.usedRowsOnlyStrategy();
        }
    },

    /**
     * Deletes only those tables which were used in data sets.
     */
    USED_TABLES_ONLY {
        @Override
        public <T> T provide(StrategyProvider<T> provider) {
            return provider.usedTablesOnlyStrategy();
        }
    },

    /**
     * This is guarding enum instance used to indicate
     * that use has not defined cleanup strategy explicitly.
     * Therefore one defined globally in <code>arquillian.xml</code>
     * should be used.
     */
    DEFAULT {
        @Override
        public <T> T provide(StrategyProvider<T> provider) {
            return provider.defaultStrategy();
        }
    };

    public abstract <T> T provide(StrategyProvider<T> provider);

    public interface StrategyProvider<T> {
        T strictStrategy();

        T usedTablesOnlyStrategy();

        T usedRowsOnlyStrategy();

        T defaultStrategy();
    }

}
