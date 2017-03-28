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
package org.arquillian.persistence.dbunit;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.arquillian.persistence.core.event.AfterPersistenceTest;
import org.arquillian.persistence.core.event.BeforePersistenceTest;
import org.arquillian.persistence.core.metadata.MetadataExtractor;
import org.arquillian.persistence.core.metadata.PersistenceExtensionFeatureResolver;
import org.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.arquillian.persistence.dbunit.configuration.DBUnitConfigurationPropertyMapper;
import org.arquillian.persistence.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.arquillian.persistence.dbunit.data.provider.DataSetProvider;
import org.arquillian.persistence.dbunit.data.provider.ExpectedDataSetProvider;
import org.arquillian.persistence.dbunit.dataset.DataSetRegister;
import org.arquillian.persistence.dbunit.exception.DBUnitConnectionException;
import org.arquillian.persistence.dbunit.exception.DBUnitInitializationException;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DBUnitPersistenceTestLifecycleHandler {

    @Inject
    private Instance<DataSource> dataSourceInstance;

    @Inject
    private Instance<MetadataExtractor> metadataExtractorInstance;

    @Inject
    private Instance<DBUnitConfiguration> dbUnitConfigurationInstance;

    @Inject
    @ClassScoped
    private InstanceProducer<DatabaseConnection> databaseConnectionProducer;

    @Inject
    @TestScoped
    private InstanceProducer<DataSetRegister> dataSetRegisterProducer;

    @Inject
    private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolverInstance;

    // ------------------------------------------------------------------------------------------------
    // Intercepting data handling events
    // ------------------------------------------------------------------------------------------------

    public void provideDatabaseConnectionAroundBeforePersistenceTest(@Observes(precedence = 100000) EventContext<BeforePersistenceTest> context) {
        createDatabaseConnection();
        context.proceed();
    }

    public void closeDatabaseConnections(@Observes(precedence = 100000) EventContext<AfterPersistenceTest> context) {
        try {
            context.proceed();
        } finally {
            closeDatabaseConnection();
        }
    }

    public void createDatasets(@Observes(precedence = 1000) EventContext<BeforePersistenceTest> context) {
        final Method testMethod = context.getEvent().getTestMethod();

        PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = persistenceExtensionFeatureResolverInstance.get();
        if (persistenceExtensionFeatureResolver.shouldSeedData()) {
            DataSetProvider dataSetProvider = new DataSetProvider(metadataExtractorInstance.get(), dbUnitConfigurationInstance.get());
            createInitialDataSets(dataSetProvider.getDescriptorsDefinedFor(testMethod));
        }

        if (persistenceExtensionFeatureResolver.shouldVerifyDataAfterTest()) {
            final ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(metadataExtractorInstance.get(), dbUnitConfigurationInstance.get());
            createExpectedDataSets(dataSetProvider.getDescriptorsDefinedFor(testMethod));
        }

        context.proceed();
    }

    // ------------------------------------------------------------------------------------------------

    private void createDatabaseConnection() {

        if (databaseConnectionProducer.get() == null) {
            configureDatabaseConnection();
        }

    }

    private void configureDatabaseConnection() {
        try {
            final DataSource dataSource = dataSourceInstance.get();
            final String schema = dbUnitConfigurationInstance.get().getSchema();
            final DatabaseConnection databaseConnection = createDatabaseConnection(dataSource, schema);
            databaseConnectionProducer.set(databaseConnection);

            final DatabaseConfig dbUnitConfig = databaseConnection.getConfig();
            dbUnitConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new DefaultDataTypeFactory());

            final Map<String, Object> properties = new DBUnitConfigurationPropertyMapper().map(dbUnitConfigurationInstance.get());
            for (Entry<String, Object> property : properties.entrySet()) {
                dbUnitConfig.setProperty(property.getKey(), property.getValue());
            }
        } catch (Exception e) {
            throw new DBUnitInitializationException("Unable to initialize database connection for DBUnit module.", e);
        }
    }

    public DatabaseConnection createDatabaseConnection(final DataSource dataSource, final String schema)
            throws DatabaseUnitException, SQLException {
        DatabaseConnection databaseConnection;
        if (schema != null && schema.length() > 0) {
            databaseConnection = new DatabaseConnection(dataSource.getConnection(), schema);
        } else {
            databaseConnection = new DatabaseConnection(dataSource.getConnection());
        }
        return databaseConnection;
    }

    private void closeDatabaseConnection() {

        try {
            final Connection connection = databaseConnectionProducer.get().getConnection();
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            throw new DBUnitConnectionException("Unable to close connection.", e);
        }

    }

    private void createInitialDataSets(Collection<DataSetResourceDescriptor> dataSetDescriptors) {
        DataSetRegister dataSetRegister = getOrCreateDataSetRegister();
        for (DataSetResourceDescriptor dataSetDescriptor : dataSetDescriptors) {
            dataSetRegister.addInitial(dataSetDescriptor.getContent());
        }
        dataSetRegisterProducer.set(dataSetRegister);
    }

    private void createExpectedDataSets(Collection<DataSetResourceDescriptor> dataSetDescriptors) {
        DataSetRegister dataSetRegister = getOrCreateDataSetRegister();
        for (DataSetResourceDescriptor dataSetDescriptor : dataSetDescriptors) {
            dataSetRegister.addExpected(dataSetDescriptor.getContent());
        }
        dataSetRegisterProducer.set(dataSetRegister);
    }

    private DataSetRegister getOrCreateDataSetRegister() {
        DataSetRegister dataSetRegister = dataSetRegisterProducer.get();
        if (dataSetRegister == null) {
            dataSetRegister = new DataSetRegister();
        }
        return dataSetRegister;
    }


}
