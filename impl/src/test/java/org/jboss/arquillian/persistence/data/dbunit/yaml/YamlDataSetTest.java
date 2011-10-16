package org.jboss.arquillian.persistence.data.dbunit.yaml;

import java.io.InputStream;

import org.jboss.arquillian.persistence.data.dbunit.DataSetAssert;
import org.jboss.arquillian.persistence.data.dbunit.TableAssert;
import org.jboss.arquillian.persistence.data.dbunit.dataset.yaml.YamlDataSet;
import org.junit.Test;

public class YamlDataSetTest
{

   @Test
   public void shouldLoadTableFromYamlFile() throws Exception
   {
      // given
      final InputStream input = getClass().getClassLoader().getResourceAsStream("one-table.yml");

      // when
      YamlDataSet yamlDataSet = new YamlDataSet(input);
      
      // then
      DataSetAssert.assertThat(yamlDataSet).hasTables("useraccount");
   }
   
   @Test
   public void shouldLoadAllColumnsForTableFromYamlFile() throws Exception
   {
      // given
      final InputStream input = getClass().getClassLoader().getResourceAsStream("one-table.yml");

      // when
      YamlDataSet yamlDataSet = new YamlDataSet(input);
      
      // then
      TableAssert.assertThat(yamlDataSet.getTable("useraccount")).hasColumns("id", "firstname", "lastname", "username", "password", "email");
   }
   
   @Test
   public void shouldLoadAllRowsForTableFromYamlFile() throws Exception
   {
      // given
      final InputStream input = getClass().getClassLoader().getResourceAsStream("one-table.yml");

      // when
      YamlDataSet yamlDataSet = new YamlDataSet(input);
      
      // then
      TableAssert.assertThat(yamlDataSet.getTable("useraccount")).hasRows(2);
   }
   
   @Test
   public void shouldLoadAllRowsWithContentForTableFromYamlFile() throws Exception
   {
      // given
      final InputStream input = getClass().getClassLoader().getResourceAsStream("one-table.yml");

      // when
      YamlDataSet yamlDataSet = new YamlDataSet(input);
      
      // then
      TableAssert.assertThat(yamlDataSet.getTable("useraccount"))
                 .hasRow("id: 1", "firstname: John", "lastname: Smith", "username: doovde", "password: password")
                 .hasRow("id: 2", "firstname: Clark", "lastname: Kent", "username: superman", "password: kryptonite", "email: arquillian@jboss.org");
   }
   
   @Test
   public void shouldLoadTwoTablesFromYamlFile() throws Exception
   {
      // given
      final InputStream input = getClass().getClassLoader().getResourceAsStream("tables.yml");

      // when
      YamlDataSet yamlDataSet = new YamlDataSet(input);
      
      // then
      DataSetAssert.assertThat(yamlDataSet).hasTables("useraccount", "testtable");
   }
   
}
