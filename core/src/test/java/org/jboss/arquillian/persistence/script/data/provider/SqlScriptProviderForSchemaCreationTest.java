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
package org.jboss.arquillian.persistence.script.data.provider;

import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.persistence.script.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.testutils.TestConfigurationLoader;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

import java.util.Collection;

public class SqlScriptProviderForSchemaCreationTest {

    private ScriptingConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultScriptingConfiguration();

    @Test
    public void should_fetch_all_scripts_defined_for_test_class_in_defined_order() throws Exception {
        // given
        TestEvent testEvent = createTestEvent("shouldPass");
        SqlScriptProvider<CreateSchema> scriptsProvider = createSqlScriptProviderFor(testEvent);

        // when
        Collection<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestClass());

        // then

        SqlScriptDescriptorAssert.assertThat(scriptDescriptors).containsExactlyFollowingFiles("one.sql", "two.sql", "three.sql");
    }

    // ----------------------------------------------------------------------------------------

    private SqlScriptProvider<CreateSchema> createSqlScriptProviderFor(TestEvent testEvent) {
        return SqlScriptProvider.createProviderForCreateSchemaScripts(testEvent.getTestClass(), defaultConfiguration);
    }

    private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException {
        TestEvent testEvent = new TestEvent(new CreateSchemaAnnotatedClass(), CreateSchemaAnnotatedClass.class.getMethod(testMethod));
        return testEvent;
    }

    @CreateSchema({"one.sql", "two.sql", "three.sql"})
    private static class CreateSchemaAnnotatedClass {
        public void shouldPass() {
        }
    }

}
