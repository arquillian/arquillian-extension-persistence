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
package org.arquillian.ape.rdbms.core.data.descriptor;

import java.util.EnumSet;
import java.util.Set;

public enum Format {
    DTD("dtd"),
    XML("xml"),
    EXCEL("xls"),
    YAML("yml"),
    JSON("json"),
    INLINE_SQL("-inline-"),
    SQL_SCRIPT("sql"),
    UNSUPPORTED("-none-");

    private static final EnumSet<Format> NOT_REAL_FILE_TYPES = EnumSet.of(UNSUPPORTED, INLINE_SQL);

    private final String fileExtension;

    Format(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String extension() {
        return fileExtension;
    }

    public static Format inferFromFile(String fileName) {
        final Set<Format> validFormats = EnumSet.complementOf(NOT_REAL_FILE_TYPES);

        for (Format format : validFormats) {
            if (fileName.endsWith(format.fileExtension)) {
                return format;
            }
        }

        return UNSUPPORTED;
    }

    public static boolean isFileType(Format format) {
        return EnumSet.complementOf(NOT_REAL_FILE_TYPES).contains(format);
    }

}
