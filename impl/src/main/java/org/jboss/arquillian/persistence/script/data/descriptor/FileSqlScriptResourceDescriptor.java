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
package org.jboss.arquillian.persistence.script.data.descriptor;

import org.jboss.arquillian.persistence.script.ScriptLoader;

/**
 *
 * SQL script file descriptor.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class FileSqlScriptResourceDescriptor extends SqlScriptResourceDescriptor
{

   public FileSqlScriptResourceDescriptor(String location)
   {
      super(location);
   }

   @Override
   public String getContent()
   {
      return ScriptLoader.loadScript(getLocation());
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }

      if (!(obj instanceof FileSqlScriptResourceDescriptor))
      {
         return false;
      }

      final FileSqlScriptResourceDescriptor other = (FileSqlScriptResourceDescriptor) obj;
      return location.equals(other.location);
   }

   @Override
   public int hashCode()
   {
      final int prime = 13;
      int result = 1;
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      return result;
   }

   @Override
   public String toString()
   {
      return this.getClass().getSimpleName() + "@" + hashCode() + "[" + location + "]";
   }

}
