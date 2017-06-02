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
package org.arquillian.integration.ape.testextension.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.arquillian.ape.rdbms.ShouldMatchDataSet;
import org.arquillian.ape.rdbms.core.event.AfterPersistenceTest;
import org.arquillian.ape.rdbms.core.metadata.DbUnitMetadataExtractor;
import org.arquillian.ape.rdbms.core.test.AssertionErrorCollector;
import org.arquillian.ape.rdbms.dbunit.DataSetComparator;
import org.arquillian.ape.rdbms.dbunit.DataSetUtils;
import org.arquillian.ape.rdbms.dbunit.configuration.DBUnitConfiguration;
import org.arquillian.ape.rdbms.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.arquillian.integration.ape.testextension.data.annotation.DatabaseShouldBeEmptyAfterTest;
import org.arquillian.integration.ape.testextension.data.annotation.DatabaseShouldContainAfterTest;
import org.arquillian.integration.ape.testextension.data.annotation.ShouldBeEmptyAfterTest;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.IColumnFilter;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;

public class DataContentVerifier {

    @Inject
    private Instance<DatabaseConnection> databaseConnection;

    @Inject
    private Instance<DBUnitConfiguration> dbunitConfiguration;

    @Inject
    private Instance<DbUnitMetadataExtractor> metadataExtractor;

    @Inject
    private Instance<AssertionErrorCollector> assertionErrorCollector;

    public void verifyDatabaseContentAfterTest(@Observes(precedence = -1000) AfterPersistenceTest afterPersistenceTest) {
        DataSetComparator dataSetComparator = new DataSetComparator(new String[] {}, new String[] {},
            Collections.<Class<? extends IColumnFilter>>emptySet());
        ShouldMatchDataSet shouldMatchDataSet =
            afterPersistenceTest.getTestMethod().getAnnotation(ShouldMatchDataSet.class);
        if (shouldMatchDataSet != null) {
            dataSetComparator = new DataSetComparator(shouldMatchDataSet.orderBy(), shouldMatchDataSet.excludeColumns(),
                Collections.<Class<? extends IColumnFilter>>emptySet());
        }
        try {
            Method testMethod = afterPersistenceTest.getTestMethod();
            DatabaseShouldBeEmptyAfterTest shouldBeEmptyAfterTest =
                testMethod.getAnnotation(DatabaseShouldBeEmptyAfterTest.class);
            final IDataSet actualContent = databaseConnection.get().createDataSet();
            if (shouldBeEmptyAfterTest != null) {
                final IDataSet filteredActualContent =
                    DataSetUtils.excludeTables(actualContent, dbunitConfiguration.get().getExcludeTablesFromCleanup());
                dataSetComparator.shouldBeEmpty(filteredActualContent, assertionErrorCollector.get());
            }

            DatabaseShouldContainAfterTest databaseShouldContain =
                testMethod.getAnnotation(DatabaseShouldContainAfterTest.class);
            if (databaseShouldContain != null) {
                final IDataSet expectedDataSet = createExpectedDataSet(afterPersistenceTest);
                dataSetComparator.compare(actualContent, expectedDataSet, assertionErrorCollector.get());
            }

            ShouldBeEmptyAfterTest shouldBeEmpty = testMethod.getAnnotation(ShouldBeEmptyAfterTest.class);
            if (shouldBeEmpty != null) {
                final IDataSet expectedDataSet = actualContent;
                for (String tableName : shouldBeEmpty.value()) {
                    dataSetComparator.shouldBeEmpty(expectedDataSet, tableName, assertionErrorCollector.get());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to verify database content after test", e);
        }
    }

    private IDataSet createExpectedDataSet(AfterPersistenceTest afterPersistenceTest) throws DataSetException {
        TestClass testClass = afterPersistenceTest.getTestClass();
        CleanupVerificationDataSetProvider cleanupVerificationDataSetProvider =
            new CleanupVerificationDataSetProvider(testClass, metadataExtractor.get(), dbunitConfiguration.get());
        Collection<DataSetResourceDescriptor> descriptors =
            cleanupVerificationDataSetProvider.getDescriptorsDefinedFor(afterPersistenceTest.getTestMethod());
        List<IDataSet> dataSets = new ArrayList<IDataSet>(descriptors.size());
        for (DataSetResourceDescriptor dataSetDescriptor : descriptors) {
            dataSets.add(dataSetDescriptor.getContent());
        }
        IDataSet expectedDataSet = DataSetUtils.mergeDataSets(dataSets);
        return expectedDataSet;
    }
}
