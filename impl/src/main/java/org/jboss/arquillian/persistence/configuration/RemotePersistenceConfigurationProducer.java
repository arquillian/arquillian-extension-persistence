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

import java.io.InputStream;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * Triggers configuration creation on the container side.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class RemotePersistenceConfigurationProducer
{
   private static final String ARQUILLIAN_CONFIG_IN_YAML = "persistence-config.yml";

   @Inject @ApplicationScoped
   InstanceProducer<PersistenceConfiguration> configurationProducer;

   public void configure(@Observes BeforeSuite beforeClassEvent)
   {
      PersistenceConfiguration configuration = (PersistenceConfiguration) new Yaml().load(loadArquillianYaml());
      configurationProducer.set(configuration);
   }

   // Private methods

   private InputStream loadArquillianYaml()
   {
      return Thread.currentThread().getContextClassLoader().getResourceAsStream(ARQUILLIAN_CONFIG_IN_YAML);
   }

}
