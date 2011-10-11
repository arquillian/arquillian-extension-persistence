package org.jboss.arquillian.persistence.configuration;

import java.io.Serializable;

/**
 * 
 * TODO extend javadoc
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceConfiguration implements Serializable
{

   private static final long serialVersionUID = -6930645145050348980L;

   private String defaultDataSource;
   
   private String initStatement;

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
   
}
