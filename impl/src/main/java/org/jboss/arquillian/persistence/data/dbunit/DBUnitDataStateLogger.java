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
package org.jboss.arquillian.persistence.data.dbunit;

import java.io.FileOutputStream;
import java.lang.reflect.Method;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.DataStateLogger;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitDataSetHandlingException;
import org.jboss.arquillian.persistence.event.CleanUpData;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

/**
 * Dumps data base state during test method invocation, covering
 * following phases:
 * <ul>
 *      <li>before seeding database using provided data sets</li>
 *      <li>after seeding database</li>
 *      <li>after test execution</li>
 *      <li>after cleaning database state</li>
 * </ul>
 * <br />
 * If not configured otherwise it will create following files in the 
 * <code>java.io.tmpdir</code> directory using following pattern:
 * <code>[full class name]#[test name]-[phase suffix].xml</code>, 
 * where phase suffix is one of the following:
 * <ul>
 *      <li>before-seed</li>
 *      <li>after-seed</li>
 *      <li>after-test</li>
 *      <li>after-clean</li>
 * </ul>
 * 
 * Created files are in {@link FlatXmlDataSet} format.
 * <br /><br /> 
 * 
 * Might be useful for test failures diagnostic / debugging.
 * <br />
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DBUnitDataStateLogger implements DataStateLogger
{

   private static final String FILENAME_PATTERN = "[%s]-%s#%s-%s.xml";
   
   @Inject @TestScoped
   private Instance<DatabaseConnection> databaseConnection;
   
   @Inject @SuiteScoped
   private Instance<PersistenceConfiguration> configuration;
   
   @Override
   public void aroundDataSeeding(@Observes EventContext<PrepareData> context)
   {
      if (!configuration.get().isDumpData())
      {
         context.proceed();
         return;
      }
      
      PrepareData event = context.getEvent();
      dumpDatabaseState(createFileName(event, "before-seed"));
      context.proceed();
      dumpDatabaseState(createFileName(event, "after-seed"));
   }

   @Override
   public void aroundCleanup(@Observes EventContext<CleanUpData> context)
   {
      if (!configuration.get().isDumpData())
      {
         context.proceed();
         return;
      }
      CleanUpData event = context.getEvent();
      dumpDatabaseState(createFileName(event, "after-test"));
      context.proceed();
      dumpDatabaseState(createFileName(event, "after-cleanup"));
   }
   
   // Private 
   
   private String createFileName(TestEvent testEvent, String phaseSuffix)
   {
      TestClass testClass = testEvent.getTestClass();
      Method testMethod = testEvent.getTestMethod();
      return String.format(FILENAME_PATTERN, System.currentTimeMillis(), testClass.getName(), testMethod.getName(), phaseSuffix); 
   }
   
   private void dumpDatabaseState(String fileName) 
   {
      final String path = configuration.get().getDumpDirectory() + "/" + fileName;
      try
      {
         IDataSet dbContent = databaseConnection.get().createDataSet();
         FlatXmlDataSet.write(dbContent, new FileOutputStream(path ));
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException("Unable to dump database state to:  " + path, e);
      }
   }

}
