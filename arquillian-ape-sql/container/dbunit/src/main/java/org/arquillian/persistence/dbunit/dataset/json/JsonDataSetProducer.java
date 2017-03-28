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
package org.jboss.arquillian.persistence.dbunit.dataset.json;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dbunit.dataset.DataSetException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Produces JSON data set from the given file.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 * @see JsonDataSet
 */
public class JsonDataSetProducer extends DataSetProducer {

    public JsonDataSetProducer(InputStream input) {
        super(input);
    }

    @Override
    Map<String, List<Map<String, String>>> loadDataSet() throws DataSetException {
        Map<String, List<Map<String, String>>> dataset;
        try {
            dataset = new ObjectMapper().readValue(input, Map.class);
        } catch (JsonParseException e) {
            throw new DataSetException("Error parsing json data set", e);
        } catch (JsonMappingException e) {
            throw new DataSetException("Error mapping json data set", e);
        } catch (IOException e) {
            throw new DataSetException("Error opening json data set", e);
        }
        return dataset;
    }

}
