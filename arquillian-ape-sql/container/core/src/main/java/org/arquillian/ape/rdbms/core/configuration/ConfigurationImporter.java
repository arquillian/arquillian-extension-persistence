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
package org.arquillian.ape.rdbms.core.configuration;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;
import org.arquillian.ape.rdbms.core.exception.PersistenceExtensionInitializationException;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

/**
 * Fetches persistence-related configuration from <code>arquillian.xml</code> or
 * property file and creates {@see PersistenceConfiguration} instance used
 * during tests execution.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class ConfigurationImporter<T extends Configuration> {

    private static final Logger log = Logger.getLogger(ConfigurationImporter.class.getName());

    private final T configuration;

    public ConfigurationImporter(T configuration) {
        this.configuration = configuration;
    }

    public void loadFromArquillianXml(String arquillianXmlFilename) {
        final InputStream arqXmlStream =
            Thread.currentThread().getContextClassLoader().getResourceAsStream(arquillianXmlFilename);
        final ArquillianDescriptor arquillianDescriptor =
            Descriptors.importAs(ArquillianDescriptor.class).fromStream(arqXmlStream);
        from(arquillianDescriptor);
    }

    public void from(ArquillianDescriptor descriptor) {
        final Map<String, String> extensionProperties =
            extractPropertiesFromDescriptor(configuration.getQualifier(), descriptor);
        createConfiguration(extensionProperties);
    }

    public void loadFromPropertyFile(String propertyFilename) {
        final Properties properties = new Properties();
        InputStream propertiesStream = null;
        try {
            propertiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFilename);
            properties.load(propertiesStream);
            from(properties);
        } catch (Exception e) {
            throw new PersistenceExtensionInitializationException(
                "Unable to load Arquillian properties in container. Missing file " + propertyFilename, e);
        } finally {
            if (propertiesStream != null) {
                try {
                    propertiesStream.close();
                } catch (IOException e) {
                    throw new PersistenceExtensionInitializationException(
                        "Failed to close the stream for file " + propertyFilename, e);
                }
            }
        }
    }

    public void from(Properties properties) {
        Map<String, String> fieldsWithValues = convertKeys(properties);
        createConfiguration(fieldsWithValues);
    }

    private Map<String, String> convertKeys(Properties properties) {
        final Map<String, String> convertedFieldsWithValues = new HashMap<String, String>();
        for (Entry<Object, Object> property : properties.entrySet()) {
            final String key = String.valueOf(property.getKey());
            final String value = String.valueOf(property.getValue());
            convertedFieldsWithValues.put(convertFromPropertyKey(key), value);
        }
        return convertedFieldsWithValues;
    }

    private String convertFromPropertyKey(String key) {
        key = key.replaceAll(configuration.getPrefix(), "");
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c == '.') {
                c = Character.toUpperCase(key.charAt(++i));
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private void createConfiguration(final Map<String, String> fieldsWithValues) {
        final ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();
        final Collection<Field> fields = ReflectionHelper.getAccessibleFields(configuration.getClass());

        for (Field field : fields) {
            final String fieldName = field.getName();
            if (fieldsWithValues.containsKey(fieldName)) {
                final String value = fieldsWithValues.get(fieldName);
                fieldsWithValues.remove(fieldName);
                final Class<?> fieldType = field.getType();
                try {
                    final Class<?> boxedFieldType = typeConverter.box(fieldType);
                    final Object convertedValue = typeConverter.convert(value, boxedFieldType);
                    if (convertedValue != null && boxedFieldType.isAssignableFrom(convertedValue.getClass())) {
                        final Method setter =
                            new PropertyDescriptor(fieldName, configuration.getClass()).getWriteMethod();
                        setter.invoke(configuration, convertedValue);
                    }
                } catch (Exception e) {
                    throw new PersistenceExtensionInitializationException("Unable to create persistence configuration.",
                        e);
                }
            }
        }
        if (!fieldsWithValues.keySet().isEmpty()) {
            reportNonExistingFields(fieldsWithValues, fields);
        }
    }

    private void reportNonExistingFields(final Map<String, String> fieldsWithValues, final Collection<Field> fields) {
        String[] knownFields = extractFieldNames(fields);
        log.warning(configuration + " does not support properties provided '" + Arrays.toString(
            fieldsWithValues.keySet().toArray())
            + ". Possible values: " + Arrays.toString(knownFields) + "'. Please revise your arquillian.xml. "
            + "For more details you can refer to the official documentation https://docs.jboss.org/author/display/ARQ/Persistence");
    }

    private Map<String, String> extractPropertiesFromDescriptor(String extensionName, ArquillianDescriptor descriptor) {
        for (ExtensionDef extension : descriptor.getExtensions()) {
            if (extensionName.equals(extension.getExtensionName())) {
                return extension.getExtensionProperties();
            }
        }

        return Collections.emptyMap();
    }

    private String[] extractFieldNames(Collection<Field> fields) {
        final String[] knownFields = new String[fields.size()];
        int index = 0;
        for (Field field : fields) {
            knownFields[index++] = field.getName();
        }
        return knownFields;
    }
}
