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
package org.jboss.arquillian.persistence.jpacacheeviction;

import static org.fest.assertions.MapAssert.entry;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.core.configuration.Configuration;
import org.junit.Test;

/**
 * @author <a href="mailto:thradec@gmail.com">Tomas Hradec</a>
 */
public class JpaCacheEvictionConfigurationTest
{

   private JpaCacheEvictionConfiguration config = new JpaCacheEvictionConfiguration();

   @Test
   public void should_have_default_values()
   {
      assertThat(config.getDefaultPhase()).isEqualTo(TestExecutionPhase.BEFORE);
      assertThat(config.getDefaultEntityManagerFactory()).isNull();
      assertThat(config.getDefaultStrategy()).isEqualTo(JpaCacheEvictionStrategyImpl.class);
   }

   @Test
   public void should_import_configuration_from_xml()
   {
      Configuration.importTo(config).loadFromArquillianXml(
            "arquillian-jpacacheeviction.xml");

      assertThat(config.getDefaultPhase()).isEqualTo(TestExecutionPhase.BEFORE);
      assertThat(config.getDefaultEntityManagerFactory()).isEqualTo("java:comp/env/ExamplePersistenceUnit");
      assertThat(config.getDefaultStrategy()).isEqualTo(JpaCacheEvictionStrategyImpl.class);
   }

   @Test
   public void should_import_configuration_from_properties()
   {
      Configuration.importTo(config).loadFromPropertyFile(
            "properties/jpacacheeviction.arquillian.persistence.properties");

      assertThat(config.getDefaultPhase()).isEqualTo(TestExecutionPhase.BEFORE);
      assertThat(config.getDefaultEntityManagerFactory()).isEqualTo("java:comp/env/ExamplePersistenceUnit");
      assertThat(config.getDefaultStrategy()).isEqualTo(JpaCacheEvictionStrategyImpl.class);
   }
   
   @Test
   public void should_export_configuration_to_properties() throws IOException 
   {
      config.setDefaultPhase(TestExecutionPhase.AFTER);
      config.setDefaultEntityManagerFactory("java:comp/env/CustomPersistenceUnit");
      
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Configuration.exportUsing(config).toProperties(output);
      
      Properties properties = new Properties();
      properties.load(new ByteArrayInputStream(output.toByteArray()));
      
      assertThat(properties).hasSize(3);
      assertThat(properties).includes(
            entry("arquillian.extension.persistence.jpacacheeviction.default.phase", config.getDefaultPhase().toString()),
            entry("arquillian.extension.persistence.jpacacheeviction.default.entity.manager.factory", config.getDefaultEntityManagerFactory()),
            entry("arquillian.extension.persistence.jpacacheeviction.default.strategy", config.getDefaultStrategy().getName()));
   }

}