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
package org.arquillian.ape.rdbms.script.data.descriptor;

import java.nio.charset.Charset;
import org.arquillian.ape.rdbms.core.dbunit.data.descriptor.Format;
import org.arquillian.ape.rdbms.script.ScriptLoader;

/**
 * SQL script file descriptor.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class FileSqlScriptResourceDescriptor extends SqlScriptResourceDescriptor {
    private final Charset charset;

    public FileSqlScriptResourceDescriptor(String location, Charset charset) {
        super(location);
        this.charset = charset;
    }

    @Override
    public Format getFormat() {
        return Format.SQL_SCRIPT;
    }

    @Override
    public String getContent() {
        return ScriptLoader.loadScript(getLocation(), charset);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FileSqlScriptResourceDescriptor)) {
            return false;
        }

        final FileSqlScriptResourceDescriptor other = (FileSqlScriptResourceDescriptor) obj;
        return location.equals(other.location);
    }

    @Override
    public int hashCode() {
        final int prime = 13;
        int result = 1;
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        return result;
    }
}
