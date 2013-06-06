/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.persistence.dbunit.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.jboss.arquillian.persistence.dbunit.exception.DBUnitConnectionException;

public class DatabaseConnectionRegistry
{

   private final Set<DatabaseConnection> connections = new HashSet<DatabaseConnection>();

   public void addConnection(DatabaseConnection connection)
   {
      this.connections.add(connection);
   }

   public void closeConnections()
   {
      for (DatabaseConnection connection : connections)
      {
         closeConnection(connection);
      }
   }

   public DatabaseConnection createDatabaseConnection(final DataSource dataSource, final String schema)
         throws DatabaseUnitException, SQLException
   {
      DatabaseConnection databaseConnection;
      if (schema != null && schema.length() > 0)
      {
         databaseConnection = new DatabaseConnection(dataSource.getConnection(), schema);
      }
      else
      {
         databaseConnection = new DatabaseConnection(dataSource.getConnection());
      }
      addConnection(databaseConnection);
      return databaseConnection;
   }

   private void closeConnection(DatabaseConnection databaseConnection)
   {
      try
      {
         final Connection connection = databaseConnection.getConnection();
         if (!connection.isClosed())
         {
            connection.close();
         }
      }
      catch (Exception e)
      {
         throw new DBUnitConnectionException("Unable to close connection.", e);
      }
   }

}
