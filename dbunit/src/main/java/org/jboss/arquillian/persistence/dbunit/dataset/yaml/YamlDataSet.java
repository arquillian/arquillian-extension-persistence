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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;

/**
 * DBUnit data set produced from YAML format. Each table has its own
 * section in the YAML document, where its' name is the root element
 * indicating that following entries are describing rows using column name : value pairs.
 * Each new row is denoted by '-', as in following example:
 * <pre><code>
 * useraccount:
 *   - id: 1
 *     firstname: Clark
 *     lastname: Kent
 *     username: superman
 *     password: kryptonite 
 * address:  
 *   - id: 1    
 *     streetname: "Kryptonite Street"    
 *     houseNumber: 7    
 *     city: Metropolis    
 *     zipCode: 1234 
 * useraccount_address: 
 *   - useraccount_id: 1
 *     addresses_id: 1  
 * </code></pre>
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class YamlDataSet extends CachedDataSet
{

   public YamlDataSet(YamlDataSetProducer producer, boolean caseSensitiveTableNames) throws DataSetException
   {
      super(producer, caseSensitiveTableNames);
   }
   
   public YamlDataSet(File file, boolean caseSensitiveTableNames) throws DataSetException, FileNotFoundException
   {
      this(new FileInputStream(file), caseSensitiveTableNames);
   }
   
   public YamlDataSet(File file) throws IOException, DataSetException
   {
      this(new FileInputStream(file), false);
   }

   public YamlDataSet(YamlDataSetProducer producer) throws DataSetException
   {
      this(producer, false);
   }

   public YamlDataSet(InputStream inputStream) throws DataSetException
   {    
      this(inputStream, false);
   }
   
   public YamlDataSet(InputStream inputStream, boolean caseSensitiveTableNames) throws DataSetException
   {
      this(new YamlDataSetProducer(inputStream), caseSensitiveTableNames);
   }
   
}
