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
package org.arquillian.ape.rdbms.dbunit;

import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.DataSeedStrategy;
import org.arquillian.ape.rdbms.core.data.DataHandler;
import org.arquillian.ape.rdbms.core.event.CleanupData;
import org.arquillian.ape.rdbms.core.event.CleanupDataUsingScript;
import org.arquillian.ape.rdbms.core.event.ExecuteScripts;
import org.arquillian.ape.rdbms.core.metadata.PersistenceExtensionFeatureResolver;
import org.arquillian.ape.rdbms.core.test.AssertionErrorCollector;
import org.arquillian.ape.rdbms.dbunit.cleanup.CleanupStrategyExecutor;
import org.arquillian.ape.rdbms.dbunit.cleanup.CleanupStrategyProvider;
import org.arquillian.ape.rdbms.dbunit.configuration.DBUnitConfiguration;
import org.arquillian.ape.rdbms.dbunit.configuration.DBUnitDataSeedStrategyProvider;
import org.arquillian.ape.rdbms.dbunit.dataset.DataSetRegister;
import org.arquillian.ape.rdbms.dbunit.event.CompareDBUnitData;
import org.arquillian.ape.rdbms.dbunit.event.PrepareDBUnitData;
import org.arquillian.ape.rdbms.dbunit.exception.DBUnitConnectionException;
import org.arquillian.ape.rdbms.dbunit.exception.DBUnitDataSetHandlingException;
import org.arquillian.ape.rdbms.dbunit.filter.TableFilterResolver;
import org.arquillian.ape.rdbms.script.ScriptExecutor;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.arquillian.ape.rdbms.script.data.descriptor.SqlScriptResourceDescriptor;
import org.arquillian.ape.rdbms.script.splitter.StatementSplitterResolver;
import org.arquillian.ape.spi.dbunit.filter.TableFilterProvider;
import org.arquillian.ape.spi.script.StatementSplitter;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ExcludeTableFilter;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

import java.sql.SQLException;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DBUnitDataHandler implements DataHandler<PrepareDBUnitData, CompareDBUnitData> {

    @Inject
    private Instance<DatabaseConnection> databaseConnection;

    @Inject
    private Instance<DataSetRegister> dataSetRegister;

    @Inject
    private Instance<AssertionErrorCollector> assertionErrorCollector;

    @Inject
    private Instance<DBUnitConfiguration> dbunitConfigurationInstance;

    @Inject
    private Instance<ScriptingConfiguration> scriptConfigurationInstance;

    @Inject
    private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolverInstance;

    @Override
    public void prepare(@Observes PrepareDBUnitData prepareDataEvent) {
        try {
            seedDatabase();
        } catch (Exception e) {
            throw new DBUnitDataSetHandlingException("Failed while seeding database.", e);
        }
    }

    @Override
    public void compare(@Observes CompareDBUnitData compareDataEvent) {
        try {
            IDataSet currentDataSet = databaseConnection.get().createDataSet();
            final String[] excludeTables = dbunitConfigurationInstance.get().getExcludeTablesFromComparisonWhenEmptyExpected();
            if (excludeTables.length != 0) {
                currentDataSet = new FilteredDataSet(new ExcludeTableFilter(excludeTables), currentDataSet);
            }
            final IDataSet expectedDataSet = DataSetUtils.mergeDataSets(dataSetRegister.get().getExpected());
            final DataSetComparator dataSetComparator = new DataSetComparator(compareDataEvent.getSortByColumns(),
                    compareDataEvent.getColumnsToExclude(), compareDataEvent.getCustomColumnFilters());
            dataSetComparator.compare(currentDataSet, expectedDataSet, assertionErrorCollector.get());
        } catch (Exception e) {
            throw new DBUnitDataSetHandlingException("Failed while comparing database state with provided data sets.", e);
        }
    }

    @Override
    public void cleanup(@Observes CleanupData cleanupDataEvent) {
        cleanDatabase(cleanupDataEvent.cleanupStrategy);
    }

    public void cleanupUsingScript(@Observes CleanupDataUsingScript cleanupDataUsingScriptEvent) {
        for (SqlScriptResourceDescriptor scriptDescriptor : cleanupDataUsingScriptEvent.getDescriptors()) {
            final String script = scriptDescriptor.getContent();
            executeScript(script);
        }
    }

    public void executeScripts(@Observes ExecuteScripts executeScriptsEvent) {
        for (SqlScriptResourceDescriptor scriptDescriptor : executeScriptsEvent.getDescriptors()) {
            final String script = scriptDescriptor.getContent();
            executeScript(script);
        }
    }

    // -- Private methods

    private void executeScript(String script) {
        try {
            final StatementSplitter statementSplitter = new StatementSplitterResolver(scriptConfigurationInstance.get()).resolve();
            final ScriptExecutor scriptExecutor = new ScriptExecutor(databaseConnection.get().getConnection(), scriptConfigurationInstance.get(), statementSplitter);
            scriptExecutor.execute(script);
        } catch (SQLException e) {
            throw new DBUnitConnectionException("Unable to execute script.", e);
        }
    }

    private void seedDatabase() throws Exception {
        final DatabaseConnection connection = databaseConnection.get();
        IDataSet initialDataSet = DataSetUtils.mergeDataSets(dataSetRegister.get().getInitial());
        if (dbunitConfigurationInstance.get().isFilterTables()) {
            final TableFilterProvider sequenceFilterProvider = new TableFilterResolver(dbunitConfigurationInstance.get()).resolve();
            final ITableFilter databaseSequenceFilter = sequenceFilterProvider.provide(connection, initialDataSet.getTableNames());
            initialDataSet = new FilteredDataSet(databaseSequenceFilter, initialDataSet);
        }
        seedingStrategy().execute(connection, initialDataSet);
    }

    private DatabaseOperation seedingStrategy() {
        final DBUnitConfiguration dbUnitConfiguration = dbunitConfigurationInstance.get();
        final DataSeedStrategy dataSeedStrategy = persistenceExtensionFeatureResolverInstance.get().getDataSeedStrategy();
        final boolean useIdentityInsert = dbUnitConfiguration.isUseIdentityInsert();
        return dataSeedStrategy.provide(new DBUnitDataSeedStrategyProvider(useIdentityInsert));
    }

    private void cleanDatabase(CleanupStrategy cleanupStrategy) {
        final CleanupStrategyExecutor cleanupStrategyExecutor = cleanupStrategy.provide(new CleanupStrategyProvider(
                databaseConnection.get(), dataSetRegister.get(), dbunitConfigurationInstance.get()));
        cleanupStrategyExecutor.cleanupDatabase(dbunitConfigurationInstance.get().getExcludeTablesFromCleanup());
    }

}
