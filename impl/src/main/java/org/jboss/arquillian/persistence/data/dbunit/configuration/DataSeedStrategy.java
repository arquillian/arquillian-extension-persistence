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
package org.jboss.arquillian.persistence.data.dbunit.configuration;

import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.operation.DatabaseOperation;

/**
*
* @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
*
*/
public enum DataSeedStrategy {

   /**
    * Performs insert of the data defined in provided data sets.
    * Default strategy.
    */
   INSERT
   {
      @Override
      public DatabaseOperation get(boolean useIdentityInsert)
      {
         if (useIdentityInsert)
         {
            return InsertIdentityOperation.INSERT;
         }
         return DatabaseOperation.INSERT;
      }

   },
   /**
    *  Performs insert of the data defined in provided data sets,
    *  after removal of all data present in the tables referred
    *  in provided files.
    */
   CLEAN_INSERT
   {
      @Override
      public DatabaseOperation get(boolean useIdentityInsert)
      {
         if (useIdentityInsert)
         {
            return InsertIdentityOperation.CLEAN_INSERT;
         }
         return DatabaseOperation.CLEAN_INSERT;
      }
   },
   /**
    * During this operation existing rows are updated and new ones are inserted.
    * Entries already existing in the database which are not defined in the provided
    * dataset are not affected.
    */
   REFRESH
   {
      @Override
      public DatabaseOperation get(boolean useIdentityInsert)
      {
         if (useIdentityInsert)
         {
            return InsertIdentityOperation.REFRESH;
         }
         return DatabaseOperation.REFRESH;
      }
   },
   /**
    * This strategy updates existing rows using data provided
    * in the datasets. If dataset contain a row which is not
    * present in the database (identified by its primary key)
    * then exception is thrown.
    */
   UPDATE
   {
      @Override
      public DatabaseOperation get(boolean useIdentityInsert)
      {
         return DatabaseOperation.UPDATE;
      }
   };

   public abstract DatabaseOperation get(boolean useIdentityInsert);

}
