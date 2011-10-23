package org.jboss.arquillian.persistence.configuration;

import java.io.Serializable;

import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.data.Format;

/**
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceConfiguration implements Serializable
{

   private static final long serialVersionUID = -6930645145050348980L;

   private String defaultDataSource;
   
   private String initStatement;

   private Format defaultDataSetFormat = Format.XML;
   
   private TransactionMode defaultTransactionMode = TransactionMode.COMMIT;
   
   private boolean dumpData;
   
   private String dumpDirectory = System.getProperty("java.io.tmpdir");
   
   public String getDefaultDataSource()
   {
      return defaultDataSource;
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
   
   
   
}
