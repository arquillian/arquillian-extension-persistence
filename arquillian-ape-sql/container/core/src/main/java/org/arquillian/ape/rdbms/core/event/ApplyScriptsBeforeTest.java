package org.arquillian.ape.rdbms.core.event;

import org.arquillian.ape.rdbms.script.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

import java.util.Collection;

public class ApplyScriptsBeforeTest extends ExecuteScripts {

    public ApplyScriptsBeforeTest(TestEvent testEvent, Collection<SqlScriptResourceDescriptor> dataSetDescriptors) {
        super(testEvent, dataSetDescriptors);
    }

}
