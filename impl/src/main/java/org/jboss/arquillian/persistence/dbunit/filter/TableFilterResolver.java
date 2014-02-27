/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.persistence.dbunit.filter;

import org.jboss.arquillian.persistence.core.util.Strings;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.spi.dbunit.filter.TableFilterProvider;
import org.jboss.arquillian.persistence.util.JavaSPIExtensionLoader;

import java.util.Collection;
import java.util.logging.Logger;

public class TableFilterResolver
{

   private static final Logger log = Logger.getLogger(TableFilterResolver.class.getName());

   private final DBUnitConfiguration dbUnitConfiguration;

   public TableFilterResolver(DBUnitConfiguration dbUnitConfiguration)
   {
      this.dbUnitConfiguration = dbUnitConfiguration;
   }

   public TableFilterProvider resolve()
   {
      if (Strings.isEmpty(dbUnitConfiguration.getCustomTableFilter()))
      {
         return new DefaultDatabaseSequenceFilterProvider();
      }

      TableFilterProvider resolved = null;
      final Collection<TableFilterProvider> databaseSequenceFilterProviders = new JavaSPIExtensionLoader().all(Thread.currentThread().getContextClassLoader(), TableFilterProvider.class);
      for (TableFilterProvider databaseSequenceFilterProvider : databaseSequenceFilterProviders)
      {
         if (databaseSequenceFilterProvider.getClass().getName().equals(dbUnitConfiguration.getCustomTableFilter()))
         {
            resolved = databaseSequenceFilterProvider;
         }
      }

      if (resolved == null)
      {
         log.warning("Unable to find sequence filter for " + dbUnitConfiguration.getCustomTableFilter() + ". Using default database sequence filter.");
         return new DefaultDatabaseSequenceFilterProvider();
      }

      return resolved;
   }

}
