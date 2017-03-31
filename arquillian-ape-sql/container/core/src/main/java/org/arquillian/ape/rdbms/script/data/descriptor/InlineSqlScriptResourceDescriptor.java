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

import org.arquillian.ape.rdbms.core.dbunit.data.descriptor.Format;

/**
 * Inline SQL script descriptor.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class InlineSqlScriptResourceDescriptor extends SqlScriptResourceDescriptor {

    private final String content;

    public InlineSqlScriptResourceDescriptor(String content) {
        super("-inline-file-");
        this.content = content;
    }

    @Override
    public Format getFormat() {
        return Format.INLINE_SQL;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof InlineSqlScriptResourceDescriptor)) {
            return false;
        }

        final InlineSqlScriptResourceDescriptor other = (InlineSqlScriptResourceDescriptor) obj;
        return content.equals(other.content);
    }

    @Override
    public int hashCode() {
        final int prime = 19;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "\n content {" + content + "}";
    }
}
