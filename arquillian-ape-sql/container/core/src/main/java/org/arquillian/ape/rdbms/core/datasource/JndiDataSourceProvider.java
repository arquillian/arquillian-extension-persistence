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
package org.arquillian.ape.rdbms.core.datasource;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.arquillian.ape.rdbms.core.exception.ContextNotAvailableException;
import org.arquillian.ape.rdbms.core.exception.DataSourceNotFoundException;
import org.arquillian.ape.spi.datasource.DataSourceProvider;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

/**
 * The JNDI data source provider, that obtains the data source from the JNDI context based on the specified name.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class JndiDataSourceProvider implements DataSourceProvider {

    /**
     * The JNDI context.
     */
    @Inject
    private Instance<Context> contextInstance;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource lookupDataSource(String dataSourceName) {

        try {
            final Context context = contextInstance.get();
            if (context == null) {
                throw new ContextNotAvailableException("No Naming Context available.");
            }
            return (DataSource) context.lookup(dataSourceName);
        } catch (NamingException e) {
            throw new DataSourceNotFoundException("Unable to find data source for given name: " + dataSourceName, e);
        }
    }
}
