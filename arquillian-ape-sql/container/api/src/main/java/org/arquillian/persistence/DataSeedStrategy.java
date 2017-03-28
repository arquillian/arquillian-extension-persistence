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
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public enum DataSeedStrategy {

    /**
     * Performs insert of the data defined in provided data sets.
     * Default strategy.
     */
    INSERT {
        @Override
        public <T> T provide(StrategyProvider<T> provider) {
            return provider.insertStrategy();
        }
    },

    /**
     * Performs insert of the data defined in provided data sets,
     * after removal of all data present in the tables referred
     * in provided files.
     */
    CLEAN_INSERT {
        @Override
        public <T> T provide(StrategyProvider<T> provider) {
            return provider.cleanInsertStrategy();
        }
    },

    /**
     * During this operation existing rows are updated and new ones are inserted.
     * Entries already existing in the database which are not defined in the provided
     * dataset are not affected.
     */
    REFRESH {
        @Override
        public <T> T provide(StrategyProvider<T> provider) {
            return provider.refreshStrategy();
        }
    },

    /**
     * This strategy updates existing rows using data provided
     * in the datasets. If dataset contain a row which is not
     * present in the database (identified by its primary key)
     * then exception is thrown.
     */
    UPDATE {
        @Override
        public <T> T provide(StrategyProvider<T> provider) {
            return provider.updateStrategy();
        }
    },

    /**
     * This is guarding enum instance used to indicate
     * that use has not defined seeding strategy explicitly.
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
        T insertStrategy();

        T cleanInsertStrategy();

        T refreshStrategy();

        T updateStrategy();

        T defaultStrategy();
    }

}
