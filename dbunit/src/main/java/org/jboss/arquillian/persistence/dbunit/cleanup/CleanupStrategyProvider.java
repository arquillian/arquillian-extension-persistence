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
package org.jboss.arquillian.persistence.dbunit.cleanup;

import org.dbunit.database.DatabaseConnection;
import org.jboss.arquillian.persistence.BuiltInCleanupStrategy.StrategyProvider;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.dataset.DataSetRegister;

public class CleanupStrategyProvider implements StrategyProvider<CleanupStrategyExecutor> {

    private final DatabaseConnection connection;

    private final DataSetRegister register;

    private final DBUnitConfiguration dbUnitConfiguration;

    public CleanupStrategyProvider(DatabaseConnection connection, DataSetRegister register,
        DBUnitConfiguration dbUnitConfiguration) {
        this.connection = connection;
        this.register = (register != null) ? register : new DataSetRegister();
        this.dbUnitConfiguration = dbUnitConfiguration;
    }

    @Override
    public CleanupStrategyExecutor strictStrategy() {
        return new StrictCleanupStrategyExecutor(connection, dbUnitConfiguration);
    }

    @Override
    public CleanupStrategyExecutor usedTablesOnlyStrategy() {
        return new UsedTablesOnlyCleanupStrategyExecutor(connection, register, dbUnitConfiguration);
    }

    @Override
    public CleanupStrategyExecutor usedRowsOnlyStrategy() {
        return new SeededDataOnlyCleanupStrategyExecutor(connection, register, dbUnitConfiguration);
    }

    @Override
    public CleanupStrategyExecutor defaultStrategy() {
        return new StrictCleanupStrategyExecutor(connection, dbUnitConfiguration);
    }
}
