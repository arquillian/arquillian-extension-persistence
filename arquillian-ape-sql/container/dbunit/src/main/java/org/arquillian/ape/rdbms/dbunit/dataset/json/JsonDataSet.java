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
package org.arquillian.ape.rdbms.dbunit.dataset.json;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;

import java.io.*;

/**
 * DBUnit data set produced from JSON format.
 * <p>
 * <pre><code>
 * {
 * 	"useraccount":
 * 	[
 *        {
 * 			"id" : 1,
 * 			"firstname" : "John",
 * 			"lastname" : "Smith",
 * 			"username" : "doovde",
 * 			"password" : "password"
 *        },
 *        {
 * 			"id" : 2,
 * 			"firstname" : "Clark",
 * 			"lastname" : "Kent",
 * 			"username" : "superman",
 * 			"password" : "kryptonite",
 * 			"email" : "arquillian@jboss.org"
 *        }
 * 	],
 *
 * 	"testtable":
 * 	[
 *        {
 * 			"id" : 1,
 * 			"value" : "doovde"
 *        },
 *        {
 * 			"id" : 2,
 * 			"value" : "kryptonite"
 *        }
 * 	]
 * }
 * </code></pre>
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class JsonDataSet extends CachedDataSet {

    public JsonDataSet(JsonDataSetProducer producer, boolean caseSensitiveTableNames) throws DataSetException {
        super(producer, caseSensitiveTableNames);
    }

    public JsonDataSet(File file, boolean caseSensitiveTableNames) throws DataSetException, FileNotFoundException {
        this(new FileInputStream(file), caseSensitiveTableNames);
    }

    public JsonDataSet(File file) throws IOException, DataSetException {
        this(new FileInputStream(file), false);
    }

    public JsonDataSet(JsonDataSetProducer producer) throws DataSetException {
        this(producer, false);
    }

    public JsonDataSet(InputStream inputStream) throws DataSetException {
        this(inputStream, false);
    }

    public JsonDataSet(InputStream inputStream, boolean caseSensitiveTableNames) throws DataSetException {
        this(new JsonDataSetProducer(inputStream), caseSensitiveTableNames);
    }

}
