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
package org.jboss.arquillian.persistence.core.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.core.command.SchemaCreationControlCommand;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class SchemaCreationCoordinator
{
   private final static  Map<String, Boolean> createdSchemas = new ConcurrentHashMap<String, Boolean>();

   public void controlSchemaCreation(@Observes SchemaCreationControlCommand command)
   {
      final String key = command.getKey();
      if (!createdSchemas.containsKey(key))
      {
         command.setResult(Boolean.FALSE);
         createdSchemas.put(key, Boolean.TRUE);
      }
      else
      {
         command.setResult(createdSchemas.get(key));
      }
   }

}
