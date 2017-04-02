/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.persistence.core.configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.arquillian.persistence.core.exception.ConfigurationExportException;

public class ConfigurationExporter<T extends Configuration> {

    private final T configuration;

    private final PropertiesSerializer propertiesSerializer;

    public ConfigurationExporter(T configuration) {
        this.configuration = configuration;
        this.propertiesSerializer = new PropertiesSerializer(configuration.getPrefix());
    }

    public void toProperties(final OutputStream output) {
        try {
            serializeFieldsToProperties(output);
        } catch (Exception e) {
            throw new ConfigurationExportException("Unable to serialize persistence configuration to property file.", e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new ConfigurationExportException(
                        "Unable to close stream after serialization of persistence configuration to property file.", e);
                }
            }
        }
    }

    private void serializeFieldsToProperties(final OutputStream output)
        throws IOException, IllegalArgumentException, IllegalAccessException {
        output.write(propertiesSerializer.serializeToProperties(mapFieldsToProperties()).toByteArray());
    }

    private Map<String, String> mapFieldsToProperties() throws IllegalArgumentException, IllegalAccessException {
        final Map<String, String> extractedValues = new HashMap<String, String>();
        final List<Field> fields = ReflectionHelper.getAccessibleFields(configuration.getClass());

        for (Field field : fields) {
            Object object = field.get(configuration);
            String key = decamelize(field.getName());
            if (object != null) {
                extractedValues.put(key, convertToPropertyValue(object));
            }
        }

        return extractedValues;
    }

    private String convertToPropertyValue(Object object) {
        String convertedValue;
        if (object instanceof Class) {
            convertedValue = ((Class<?>) object).getName();
        } else {
            convertedValue = object.toString();
        }
        if (String[].class.isInstance(object)) {
            convertedValue = Arrays.toString((String[]) object);
            convertedValue = convertedValue.replace("[", "")
                .replace("]", "")
                .replace(", ", ",");
        }
        return convertedValue;
    }

    private String decamelize(String key) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('.').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
