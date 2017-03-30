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
package org.arquillian.ape.rdbms.dbunit.data.descriptor;

import org.arquillian.ape.rdbms.core.data.descriptor.ResourceDescriptor;
import org.arquillian.ape.rdbms.core.dbunit.data.descriptor.Format;
import org.arquillian.ape.rdbms.core.dbunit.dataset.DataSetBuilder;
import org.dbunit.dataset.IDataSet;


/**
 * Contains information about the file - it's location and {@link Format format} inferred from it's name.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DataSetResourceDescriptor extends ResourceDescriptor<IDataSet> {

    private final Format format;

    public DataSetResourceDescriptor(String location, Format format) {
        super(location);
        this.format = format;
    }

    @Override
    public Format getFormat() {
        return format;
    }

    @Override
    public IDataSet getContent() {
        return DataSetBuilder.builderFor(format).build(location);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DataSetResourceDescriptor)) {
            return false;
        }

        final DataSetResourceDescriptor other = (DataSetResourceDescriptor) obj;
        return location.equals(other.location) && format.equals(other.format);
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 1;
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        return result;
    }

}
