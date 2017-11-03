package org.arquillian.ape.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SystemPropertyResolver {
    String resolveValue(final String property) {
        PropertyValueTuple tuple = new PropertyValueTuple(property).invoke();
        String propertyValue = System.getProperty(tuple.getPropertyName());
        if (propertyValue == null) {
            propertyValue = System.getenv(tuple.getPropertyName());
        }
        if (propertyValue == null) {
            propertyValue = tuple.getDefaultValue();
        }
        if (propertyValue == null) {
            throw new RuntimeException("Could not resolve property \"" + tuple.getPropertyName()
                + "\" in the system properties or environment variables and no default value is supplied");
        }
        return propertyValue;
    }

    boolean propertyDefined(String property) {
        String propertyValue = System.getProperty(property);
        if (propertyValue == null) {
            propertyValue = System.getenv(property);
        }
        return propertyValue != null;
    }

    private class PropertyValueTuple {
        private String propertyName;
        private String defaultValue;

        PropertyValueTuple(String property) {
            this.propertyName = property;
            this.defaultValue = null;
        }

        String getPropertyName() {
            return propertyName;
        }

        String getDefaultValue() {
            return defaultValue;
        }

        PropertyValueTuple invoke() {
            if (propertyName.contains(":")) {
                String[] kv = splitWorker(propertyName, ':', true);
                propertyName = kv[0];
                if (kv.length > 1) {
                    defaultValue = String.join(":", Arrays.copyOfRange(kv, 1, kv.length));
                }
            }
            return this;
        }

        private String[] splitWorker(final String str, final char separator, final boolean preserveAllTokens) {
            // Performance tuned for 2.0 (JDK1.4)

            if (str == null) {
                return null;
            }
            final int len = str.length();
            if (len == 0) {
                return new String[0];
            }
            final List<String> list = new ArrayList<>();
            int i = 0, start = 0;
            boolean match = false;
            boolean lastMatch = false;
            while (i < len) {
                if (str.charAt(i) == separator) {
                    if (match || preserveAllTokens) {
                        list.add(str.substring(start, i));
                        match = false;
                        lastMatch = true;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
            if (match || preserveAllTokens && lastMatch) {
                list.add(str.substring(start, i));
            }
            return list.toArray(new String[list.size()]);
        }
    }
}
