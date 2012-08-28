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
package org.jboss.arquillian.persistence.core.command;


import org.jboss.arquillian.container.test.impl.client.deployment.command.AbstractCommand;


/**
 * Command used to communicate between local test executor and remote
 * container to obtain information if schema has been already created
 * before the first test execution.
 *
 * Such a construct exists due to the lack of BeforeClass hooks
 * while executing tests in the container.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class SchemaCreationControlCommand extends AbstractCommand<Boolean>
{

   private static final long serialVersionUID = 804375658347055929L;

   private final String key;

   public SchemaCreationControlCommand(String key)
   {
      super();
      this.key = key;
   }

   public String getKey()
   {
      return key;
   }



}
