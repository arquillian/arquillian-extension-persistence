package org.jboss.arquillian.persistence.data.dbunit.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Table
{
   private final String tableName;
   
   private Set<String> columns = new HashSet<String>();
   
   private List<Row> rows = new ArrayList<Row>();

   public Table(String tableName)
   {
      this.tableName = tableName;
   }

   public void addRows(Collection<Row> rows)
   {
      this.rows.addAll(rows);
   }

   public void addColumns(Collection<String> columns)
   {
      this.columns.addAll(columns);
   }
   
   public String getTableName()
   {
      return tableName;
   }
   
   public Set<String> getColumns()
   {
      return Collections.unmodifiableSet(columns);
   }
   
   public List<Row> getRows()
   {
      return Collections.unmodifiableList(rows);
   }
   
}
