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
package org.jboss.arquillian.persistence.core.configuration;

import java.io.Serializable;

import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.DataSeedStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceConfiguration extends Configuration implements Serializable
{

   private static final long serialVersionUID = -6930645145050348980L;

   private String defaultDataSource;

   private TransactionMode defaultTransactionMode = TransactionMode.COMMIT;

   private DataSeedStrategy defaultDataSeedStrategy = DataSeedStrategy.INSERT;

   private boolean dumpData = false;

   private String dumpDirectory = System.getProperty("java.io.tmpdir");

   private TestExecutionPhase defaultCleanupPhase = TestExecutionPhase.AFTER;

   private CleanupStrategy defaultCleanupStrategy = CleanupStrategy.STRICT;

   public PersistenceConfiguration()
   {
      super("persistence", "arquillian.extension.persistence.");
   }

   // Private methods

   private boolean isDefined(String toVerify)
   {
      return toVerify != null && !"".equals(toVerify.trim());
   }

   // Accessors

   public String getDefaultDataSource()
   {
      return defaultDataSource;
   }

   /**
    * @param defaultDataSource Name of the default data source used to interact with the database
    * (seeding, comparing etc). Required if not specified by using {@link DataSource} annotation.
    */
   public void setDefaultDataSource(String defaultDataSource)
   {
      this.defaultDataSource = defaultDataSource;
   }

   public boolean isDefaultDataSourceDefined()
   {
      return isDefined(defaultDataSource);
   }

   public TransactionMode getDefaultTransactionMode()
   {
      return defaultTransactionMode;
   }

   /**
    * @param defaultTransactionMode Transaction mode for running the tests if not specified explicitly by using {@link Transactional}.
    * Possible values: {@link TransactionMode#COMMIT}, {@link TransactionMode#ROLLBACK} or {@link TransactionMode#DISABLED}.
    * Default - {@link TransactionMode#COMMIT}
    */
   public void setDefaultTransactionMode(TransactionMode defaultTransactionMode)
   {
      this.defaultTransactionMode = defaultTransactionMode;
   }

   public boolean isDumpData()
   {
      return dumpData;
   }

   /**
    * @param dumpData Enables database state dumping in following phases BEFORE_SEED, AFTER_SEED, BEFORE_CLEAN, AFTER_CLEAN.
    * Might be handy for debugging. Default value is <code>false</code>.
    */
   public void setDumpData(boolean dumpData)
   {
      this.dumpData = dumpData;
   }

   public String getDumpDirectory()
   {
      return dumpDirectory;
   }

   /**
    * @param dumpDirectory Folder where all database dumps will be stored.
    * Default value is OS-specific temporary directory defined in property <code>java.io.tmpdir</code>.
    */
   public void setDumpDirectory(String dumpDirectory)
   {
      if (dumpDirectory.endsWith("/"))
      {
         dumpDirectory = dumpDirectory.substring(0, dumpDirectory.length() - 2);
      }
      this.dumpDirectory = dumpDirectory;
   }

   public TestExecutionPhase getDefaultCleanupPhase()
   {
      return defaultCleanupPhase;
   }

   /**
    * @param defaultCleanupPhase Defines default cleanup phase.
    * If not specified it's assumed to be AFTER test method.
    */
   public void setDefaultCleanupPhase(TestExecutionPhase defaultCleanupPhase)
   {
      this.defaultCleanupPhase = defaultCleanupPhase;
   }

   public CleanupStrategy getDefaultCleanupStrategy()
   {
      return defaultCleanupStrategy;
   }

   /**
    * @param defaultCleanupStrategy Defines strategy of cleaninig database content for the test.
    * Default value is {@link CleanupStrategy#STRICT}
    */
   public void setDefaultCleanupStrategy(CleanupStrategy defaultCleanupStrategy)
   {
      this.defaultCleanupStrategy = defaultCleanupStrategy;
   }

   public DataSeedStrategy getDefaultDataSeedStrategy()
   {
      return defaultDataSeedStrategy;
   }

   /**
    * @param strategy Defines strategy of inserting data to the data store.
    * Default value is {@link DataSeedStrategy#INSERT}
    *
    * @see DataSeedStrategy
    */
   public void setDataSeedStrategy(DataSeedStrategy strategy)
   {
      setDefaultDataSeedStrategy(strategy);
   }

   /**
    * @param defaultDataSeedStrategy Defines strategy of inserting data to the data store.
    * Default value is {@link DataSeedStrategy#INSERT}
    *
    * @see DataSeedStrategy
    */
   public void setDefaultDataSeedStrategy(DataSeedStrategy defaultDataSeedStrategy)
   {
      this.defaultDataSeedStrategy = defaultDataSeedStrategy;
   }

}
