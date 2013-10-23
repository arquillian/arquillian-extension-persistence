/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.persistence.spi.datasource;

import javax.sql.DataSource;

/**
 * The data source provider. The concrete implementation of this class may handle the data source lookup in various way
 * i.e. using the JNDI Service Locator or for instance the Spring application context to obtain the Spring configured
 * data source bean.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public interface DataSourceProvider {

    /**
     * Lookups the {@link DataSource} instance by the specific data source name. If the method can not find the data
     * source with the given name it should return {@link null} meaning that no data source could be found.
     *
     * @return the data source matching the specified name, or null if none data source was found
     */
    DataSource lookupDataSource(String dataSourceName);
}
