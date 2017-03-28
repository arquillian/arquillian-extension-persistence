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
package org.arquillian.ape.rdbms.core.client;

import org.arquillian.ape.rdbms.core.command.DumpDataCommand;
import org.arquillian.ape.rdbms.core.data.dump.DataDump;
import org.arquillian.ape.rdbms.core.exception.DatabaseDumpException;
import org.jboss.arquillian.core.api.annotation.Observes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Writes database state dumped during in-container test execution
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DatabaseStateDumper {
    public void dump(@Observes DumpDataCommand dumpDataCommand) {
        final DataDump dataDump = dumpDataCommand.getDumpData();
        Writer writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(dataDump.getPath()));
            writer.write(dataDump.getDataSet());
        } catch (Exception e) {
            throw new DatabaseDumpException("Unable to dump database state to " + dataDump.getPath(), e);
        } finally {
            dumpDataCommand.setResult(true);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new DatabaseDumpException("Unable to close writer.", e);
                }
            }
        }
    }

}
