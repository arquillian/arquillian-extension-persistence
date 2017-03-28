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
package org.arquillian.persistence.dbunit.configuration;

import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.operation.DatabaseOperation;
import org.arquillian.persistence.DataSeedStrategy.StrategyProvider;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DBUnitDataSeedStrategyProvider implements StrategyProvider<DatabaseOperation> {

    private final boolean useIdentityInsert;

    public DBUnitDataSeedStrategyProvider(boolean useIdentityInsert) {
        this.useIdentityInsert = useIdentityInsert;
    }

    @Override
    public DatabaseOperation insertStrategy() {
        if (useIdentityInsert) {
            return InsertIdentityOperation.INSERT;
        }
        return DatabaseOperation.INSERT;
    }

    @Override
    public DatabaseOperation cleanInsertStrategy() {
        if (useIdentityInsert) {
            return InsertIdentityOperation.CLEAN_INSERT;
        }
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    public DatabaseOperation refreshStrategy() {
        if (useIdentityInsert) {
            return InsertIdentityOperation.REFRESH;
        }
        return DatabaseOperation.REFRESH;
    }

    @Override
    public DatabaseOperation updateStrategy() {
        return DatabaseOperation.UPDATE;
    }

    @Override
    public DatabaseOperation defaultStrategy() {
        return DatabaseOperation.INSERT;
    }

}
