/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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
package org.arquillian.ape.rdbms.dbunit.data.provider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.rdbms.core.data.provider.ResourceProvider;
import org.arquillian.ape.rdbms.core.dbunit.data.descriptor.Format;
import org.arquillian.ape.rdbms.core.exception.UnsupportedDataFormatException;
import org.arquillian.ape.rdbms.core.metadata.DbUnitMetadataExtractor;
import org.arquillian.ape.rdbms.dbunit.configuration.DBUnitConfiguration;
import org.arquillian.ape.rdbms.dbunit.data.descriptor.DataSetResourceDescriptor;
import org.arquillian.ape.rdbms.dbunit.data.naming.DataSetFileNamingStrategy;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DataSetProvider extends ResourceProvider<DataSetResourceDescriptor> {

    private final DBUnitConfiguration configuration;

    public DataSetProvider(DbUnitMetadataExtractor metadataExtractor, DBUnitConfiguration configuration) {
        super(UsingDataSet.class, metadataExtractor);
        this.configuration = configuration;
    }

    @Override
    protected DataSetResourceDescriptor createDescriptor(String resource) {
        return new DataSetResourceDescriptor(determineLocation(resource), inferFormat(resource));
    }

    @Override
    protected String defaultLocation() {
        return configuration.getDefaultDataSetLocation();
    }

    @Override
    protected String defaultFileName() {
        Format format = configuration.getDefaultDataSetFormat();
        return new DataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass());
    }

    @Override
    public Collection<String> getResourceFileNames(Method testMethod) {
        UsingDataSet dataAnnotation = getResourceAnnotation(testMethod);
        String[] specifiedFileNames = dataAnnotation.value();
        if (specifiedFileNames.length == 0 || "".equals(specifiedFileNames[0].trim())) {
            return singletonList(getDefaultFileName(testMethod));
        }
        return asList(specifiedFileNames);
    }

    // Private methods

    private Format inferFormat(String dataFileName) {
        Format format = Format.inferFromFile(dataFileName);
        if (Format.UNSUPPORTED.equals(format)) {
            throw new UnsupportedDataFormatException("File " + dataFileName + " is not supported as data set format.");
        }
        return format;
    }

    Collection<Format> getDataFormats(Method testMethod) {
        final List<Format> formats = new ArrayList<Format>();
        for (String dataFileName : getResourceFileNames(testMethod)) {
            formats.add(inferFormat(dataFileName));
        }
        return formats;
    }

    private UsingDataSet getResourceAnnotation(Method testMethod) {
        return metadataExtractor.usingDataSet().fetchUsingFirst(testMethod);
    }

    private String getDefaultFileName(Method testMethod) {
        Format format = configuration.getDefaultDataSetFormat();

        if (metadataExtractor.usingDataSet().isDefinedOn(testMethod)) {
            return new DataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass(), testMethod);
        }

        return new DataSetFileNamingStrategy(format).createFileName(metadataExtractor.getJavaClass());
    }
}
