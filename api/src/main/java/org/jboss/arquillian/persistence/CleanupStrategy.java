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
package org.jboss.arquillian.persistence;


/**
 * Defines strategy to be applied for {@link @Cleanup} operation.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public enum CleanupStrategy
{
   /**
    * Cleans entire database.
    * Might require turning off database constraints (e.g. referential integrity).
    */
   STRICT,
   /**
    * Deletes only those entries which were defined in data sets.
    */
   USED_ROWS_ONLY,
   /**
    * Deletes only those tables which were used in data sets.
    */
   USED_TABLES_ONLY;

   public static CleanupStrategy getDefault()
   {
      return STRICT;
   }


}
