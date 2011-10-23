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
package org.jboss.arquillian.persistence.data;

public class DataSetDescriptor
{

   private final String fileName;
   
   private final Format format;

   public DataSetDescriptor(String fileName, Format format)
   {
      this.fileName = fileName;
      this.format = format;
   }

   public String getFileName()
   {
      return fileName;
   }
   
   public Format getFormat()
   {
      return format;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (!(obj instanceof DataSetDescriptor))
      {
         return false;
      }
      
      final DataSetDescriptor other = (DataSetDescriptor) obj;
      return fileName.equals(other.fileName) && format.equals(other.format);
   }
   
   @Override
   public int hashCode()
   {
      final int prime = 17;
      int result = 1;
      result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
      result = prime * result + ((format == null) ? 0 : format.hashCode());
      return result;
   }
   
}
