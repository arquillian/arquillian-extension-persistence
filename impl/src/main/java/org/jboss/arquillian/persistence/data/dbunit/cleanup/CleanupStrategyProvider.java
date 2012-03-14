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
package org.jboss.arquillian.persistence.data.dbunit.cleanup;

import org.dbunit.database.DatabaseConnection;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.data.dbunit.dataset.DataSetRegister;

public class CleanupStrategyProvider
{

   private final DatabaseConnection connection;

   private final DataSetRegister register;

   public CleanupStrategyProvider(DatabaseConnection connection, DataSetRegister register)
   {
      this.connection = connection;
      this.register = register;
   }

   public CleanupStrategyExecutor create(CleanupStrategy strategy)
   {

      switch (strategy)
      {
         case STRICT:
            return createStrictCleanupStrategyExecutor();
         case USED_ROWS_ONLY:
            return createSeededDataOnlyCleanupStrategyExecutor();
         case USED_TABLES_ONLY:
            return createUsedTablesOnlyCleanupStrategyExecutor();
      }

      throw new IllegalArgumentException("Unable to resolve strategy for " + strategy);

   }

   private UsedTablesOnlyCleanupStrategyExecutor createUsedTablesOnlyCleanupStrategyExecutor()
   {
      return new UsedTablesOnlyCleanupStrategyExecutor(connection, register);
   }

   private SeededDataOnlyCleanupStrategyExecutor createSeededDataOnlyCleanupStrategyExecutor()
   {
      return new SeededDataOnlyCleanupStrategyExecutor(connection, register);
   }

   private CleanupStrategyExecutor createStrictCleanupStrategyExecutor()
   {
      return new StrictCleanupStrategyExecutor(connection);
   }

}
