package org.jboss.arquillian.persistence.data.dbunit.dataset.yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.yaml.snakeyaml.Yaml;

public class YamlDataSetProducer implements IDataSetProducer
{

   private boolean caseSensitiveTableNames;

   private IDataSetConsumer consumer = new DefaultConsumer();

   private final InputStream input;

   public YamlDataSetProducer(InputStream inputStream)
   {
      this.input = inputStream;
   }

   @Override
   public void setConsumer(IDataSetConsumer consumer)
   {
      this.consumer = consumer;
   }

   @Override
   public void produce() throws DataSetException
   {
      consumer.startDataSet();

      @SuppressWarnings("unchecked")
      final List<Table> tables = createTables((Map<String, List<Map<String, String>>>) new Yaml().load(input));

      for (Table table : tables)
      {
         ITableMetaData tableMetaData = createTableMetaData(table);
         consumer.startTable(tableMetaData);
         for (Row row : table.rows)
         {
            List<String> values = new ArrayList<String>();
            for (Column column : tableMetaData.getColumns())
            {
               values.add(row.valueOf(column.getColumnName()));
            }
            consumer.row(values.toArray());
         }
         
         consumer.endTable();
      }
      
      consumer.endDataSet();

   }

   private ITableMetaData createTableMetaData(Table table)
   {
      return new DefaultTableMetaData(table.tableName, createColumns(table.columns));
   }

   private Column[] createColumns(Collection<String> columnNames)
   {
      final List<Column> columns = new ArrayList<Column>();
      for (String columnName : columnNames)
      {
         Column column = new Column(columnName, DataType.UNKNOWN);
         columns.add(column);
      }
      return columns.toArray(new Column[columns.size()]);
   }

   private List<Table> createTables(Map<String, List<Map<String, String>>> yamlStructure)
   {
      List<Table> tables = new ArrayList<Table>();
      for (Map.Entry<String, List<Map<String, String>>> entry : yamlStructure.entrySet())
      {
         Table table = new Table(entry.getKey());
         table.addColumns(extractColumns(entry.getValue()));
         table.addRows(extractRows(entry.getValue()));
         tables.add(table);
      }
      return tables;
   }

   private Collection<Row> extractRows(List<Map<String, String>> rows)
   {
      final List<Row> extractedRows = new ArrayList<Row>();
      for (Map<String, String> row : rows)
      {
         extractedRows.add(new Row(row));
      }
      return extractedRows;
   }
   
   private Collection<String> extractColumns(List<Map<String, String>> rows)
   {
      final Set<String> columns = new HashSet<String>();
      for (Map<String, String> row : rows)
      {
         columns.addAll(row.keySet());
      }
      return columns;
   }

   private static class Table
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
      
   }
   
   private static class Row
   {
      private final Map<String, String> cells = new HashMap<String, String>();
      
      public Row(Map<String, String> cells)
      {
         this.cells.putAll(cells);
      }

      public String valueOf(String name)
      {
         return String.valueOf(cells.get(name));
      }
   }
   
   /// Getters & Setters
   
   public boolean isCaseSensitiveTableNames()
   {
      return caseSensitiveTableNames;
   }

   public void setCaseSensitiveTableNames(boolean caseSensitiveTableNames)
   {
      this.caseSensitiveTableNames = caseSensitiveTableNames;
   }

}
