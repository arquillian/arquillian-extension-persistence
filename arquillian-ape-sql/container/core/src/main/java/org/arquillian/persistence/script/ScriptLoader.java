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
package org.jboss.arquillian.persistence.script;

import org.jboss.arquillian.persistence.core.exception.ScriptLoadingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public final class ScriptLoader {

    public static String loadScript(String location, Charset charset) {
        final StringBuilder builder = new StringBuilder();

        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);

        BufferedReader reader = null;
        String line;

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            while ((line = reader.readLine()) != null) {
                builder.append(line)
                        .append("\r\n");
            }
        } catch (Exception e) {
            throw new ScriptLoadingException("Failed loading script " + location, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new ScriptLoadingException("Unable to close script.", e);
                }
            }
        }

        return builder.toString();
    }

    public static boolean isSqlScriptFile(String script) {
        if (script == null) {
            return false;
        }
        return script.endsWith(".sql");
    }

}
