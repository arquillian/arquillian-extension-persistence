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
package org.jboss.arquillian.persistence.dbunit.dataset.yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import org.jboss.arquillian.persistence.dbunit.dataset.Row;
import org.jboss.arquillian.persistence.dbunit.dataset.Table;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

/**
 * Produces YAML data set from the given file.
 *
 * @see YamlDataSet
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
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
      final List<Table> tables = createTables((Map<String, List<Map<String, String>>>) createYamlReader().load(input));

      for (Table table : tables)
      {
         ITableMetaData tableMetaData = createTableMetaData(table);
         consumer.startTable(tableMetaData);
         for (Row row : table.getRows())
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

   public Yaml createYamlReader()
   {
      final Yaml yaml = new Yaml(new Constructor(), new Representer(), new DumperOptions(),
            new Resolver()
      {
         @Override
         protected void addImplicitResolvers()
         {
            // Intentionally left TIMESTAMP as string to let DBUnit deal with the conversion
            addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO");
            addImplicitResolver(Tag.INT, INT, "-+0123456789");
            addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
            addImplicitResolver(Tag.MERGE, MERGE, "<");
            addImplicitResolver(Tag.NULL, NULL, "~nN\0");
            addImplicitResolver(Tag.NULL, EMPTY, null);
            addImplicitResolver(Tag.VALUE, VALUE, "=");
            addImplicitResolver(Tag.YAML, YAML, "!&*");
         }
      });
      return yaml;
   }

   private ITableMetaData createTableMetaData(Table table)
   {
      return new DefaultTableMetaData(table.getTableName(), table.getColumns().toArray(new Column[table.getColumns().size()]));
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

   private Collection<Column> extractColumns(List<Map<String, String>> rows)
   {
      final Set<Column> columns = new HashSet<Column>();
      for (Map<String, String> row : rows)
      {
         columns.addAll(extractColumnsFromDatasetRow(row));
      }
      return columns;
   }

   private Collection<Column> extractColumnsFromDatasetRow(Map<String, String> row)
   {
      final Set<Column> columns = new HashSet<Column>();
      for (Map.Entry<String, String> currentEntry : row.entrySet()) {
         String columnName = currentEntry.getKey();
         Object value = currentEntry.getValue();
         DataType dataType = null;

         if (value instanceof Integer) {
            dataType = DataType.INTEGER;
         } else {
            dataType = DataType.VARCHAR;
         }

         columns.add(new Column(columnName, dataType));
      }

      return columns;
   }

   // Getters & Setters

   public boolean isCaseSensitiveTableNames()
   {
      return caseSensitiveTableNames;
   }

   public void setCaseSensitiveTableNames(boolean caseSensitiveTableNames)
   {
      this.caseSensitiveTableNames = caseSensitiveTableNames;
   }

}
