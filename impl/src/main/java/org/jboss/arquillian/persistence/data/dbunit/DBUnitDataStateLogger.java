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

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.jboss.arquillian.container.test.spi.command.CommandService;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.persistence.command.DumpDataCommand;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.data.dbunit.exception.DBUnitDataSetHandlingException;
import org.jboss.arquillian.persistence.data.dump.DataDump;
import org.jboss.arquillian.persistence.data.dump.DataStateLogger;
import org.jboss.arquillian.persistence.event.CleanupData;
import org.jboss.arquillian.persistence.event.PrepareData;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

/**
 * Dumps database state during test method invocation, covering
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
 * Created files are in DBUnit {@link FlatXmlDataSet} format.
 * <br /><br />
 *
 * This feature might be useful for test failures diagnostic / debugging.
 * <br />
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */

public class DBUnitDataStateLogger implements DataStateLogger
{

   private static final String FILENAME_PATTERN = "[%s]-%s#%s-%s.xml";

   @Inject
   private Instance<DatabaseConnection> databaseConnection;

   @Inject
   private Instance<PersistenceConfiguration> configuration;

   @Inject
   private Instance<CommandService> commandService;

   @Override
   public void aroundDataSeeding(@Observes EventContext<PrepareData> context)
   {
      if (!configuration.get().isDumpData())
      {
         context.proceed();
         return;
      }

      PrepareData event = context.getEvent();
      dumpDatabaseState(event, Phase.BEFORE_SEED);
      context.proceed();
      dumpDatabaseState(event, Phase.AFTER_SEED);
   }

   @Override
   public void aroundCleanup(@Observes EventContext<CleanupData> context)
   {
      if (!configuration.get().isDumpData())
      {
         context.proceed();
         return;
      }
      CleanupData event = context.getEvent();
      dumpDatabaseState(event, Phase.AFTER_TEST);
      context.proceed();
      dumpDatabaseState(event, Phase.AFTER_CLEAN);
   }

   // Private

   private String createFileName(TestEvent testEvent, String phaseSuffix)
   {
      TestClass testClass = testEvent.getTestClass();
      Method testMethod = testEvent.getTestMethod();
      return String.format(FILENAME_PATTERN, System.currentTimeMillis(), testClass.getName(), testMethod.getName(), phaseSuffix);
   }

   private void dumpDatabaseState(TestEvent event, Phase phase)
   {
      final String path = configuration.get().getDumpDirectory() + "/" + createFileName(event, phase.getName());
      try
      {
         final IDataSet dbContent = databaseConnection.get().createDataSet();
         DataDump dumpData = createDataDump(path, dbContent);
         commandService.get().execute(new DumpDataCommand(dumpData));
      }
      catch (Exception e)
      {
         throw new DBUnitDataSetHandlingException("Unable to dump database state to folder " + path, e);
      }
   }

   private DataDump createDataDump(final String path, final IDataSet dbContent) throws IOException, DataSetException
   {
      StringWriter stringWriter = new StringWriter();
      FlatXmlDataSet.write(dbContent, stringWriter);
      DataDump dumpData = new DataDump(stringWriter.toString(), path);
      stringWriter.close();
      return dumpData;
   }

   private static enum Phase
   {
      BEFORE_SEED("before-seed"),
      AFTER_SEED("after-seed"),
      AFTER_TEST("after-test"),
      AFTER_CLEAN("after-clean");

      private final String name;

      private Phase(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

   }

}
