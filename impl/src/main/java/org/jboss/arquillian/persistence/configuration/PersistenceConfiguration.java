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
package org.jboss.arquillian.persistence.configuration;

import java.io.Serializable;

import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.data.descriptor.Format;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceConfiguration implements Serializable
{

   private static final long serialVersionUID = -6930645145050348980L;

   private String defaultDataSource;

   private String defaultDataSetLocation = "datasets/";

   private String defaultSqlScriptLocation = "scripts/";

   private String initStatement;

   private String cleanupStatement;

   private Format defaultDataSetFormat = Format.XML;

   private TransactionMode defaultTransactionMode = TransactionMode.COMMIT;

   private boolean dumpData = false;

   private String dumpDirectory = System.getProperty("java.io.tmpdir");

   private String userTransactionJndi = "java:comp/UserTransaction";

   private boolean excludePoi = false; 
   
   private String escapePattern = "";

   // Accessors

   public String getDefaultDataSource()
   {
      return defaultDataSource;
   }

   public String getDefaultDataSetLocation()
   {
      return defaultDataSetLocation;
   }

   public void setDefaultDataSetLocation(String defaultDataSetLocation)
   {
      this.defaultDataSetLocation = defaultDataSetLocation;
   }

   public void setDefaultDataSource(String defaultDataSource)
   {
      this.defaultDataSource = defaultDataSource;
   }

   public boolean isDefaultDataSourceDefined()
   {
      return isDefined(defaultDataSource);
   }

   public String getInitStatement()
   {
      return initStatement;
   }

   public void setInitStatement(String initStatement)
   {
      this.initStatement = initStatement;
   }

   public String getCleanupStatement()
   {
      return cleanupStatement;
   }

   public void setCleanupStatement(String cleanupStatement)
   {
      this.cleanupStatement = cleanupStatement;
   }

   public boolean isInitStatementDefined()
   {
      return isDefined(initStatement);
   }

   private boolean isDefined(String toVerify)
   {
      return toVerify != null && !"".equals(toVerify.trim());
   }

   public Format getDefaultDataSetFormat()
   {
      return defaultDataSetFormat;
   }

   public void setDefaultDataSetFormat(Format defaultDataSetFormat)
   {
      this.defaultDataSetFormat = defaultDataSetFormat;
   }

   public TransactionMode getDefaultTransactionMode()
   {
      return defaultTransactionMode;
   }

   public void setDefaultTransactionMode(TransactionMode defaultTransactionMode)
   {
      this.defaultTransactionMode = defaultTransactionMode;
   }

   public boolean isDumpData()
   {
      return dumpData;
   }

   public void setDumpData(boolean dumpData)
   {
      this.dumpData = dumpData;
   }

   public String getDumpDirectory()
   {
      return dumpDirectory;
   }

   public void setDumpDirectory(String dumpDirectory)
   {
      if (dumpDirectory.endsWith("/"))
      {
         dumpDirectory = dumpDirectory.substring(0, dumpDirectory.length() - 2);
      }
      this.dumpDirectory = dumpDirectory;
   }

   public String getUserTransactionJndi()
   {
      return userTransactionJndi;
   }

   public void setUserTransactionJndi(String userTransactionJndi)
   {
      this.userTransactionJndi = userTransactionJndi;
   }

   public String getDefaultSqlScriptLocation()
   {
      return defaultSqlScriptLocation;
   }

   public void setDefaultSqlScriptLocation(String defaultSqlScriptLocation)
   {
      this.defaultSqlScriptLocation = defaultSqlScriptLocation;
   }

   public boolean isExcludePoi()
   {
      return excludePoi;
   }

   public void setExcludePoi(boolean excludePoi)
   {
      this.excludePoi = excludePoi;
   }

   public String getEscapePattern() 
   {
      return escapePattern;
   }

   public void setEscapePattern(String escapePattern) 
   {
      this.escapePattern = escapePattern;
   }
}
