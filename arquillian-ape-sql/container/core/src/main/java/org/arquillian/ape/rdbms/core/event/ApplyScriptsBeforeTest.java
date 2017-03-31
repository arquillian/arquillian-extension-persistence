package org.arquillian.ape.rdbms.core.event;

import java.util.Collection;
import org.arquillian.ape.rdbms.script.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class ApplyScriptsBeforeTest extends ExecuteScripts {

    public ApplyScriptsBeforeTest(TestEvent testEvent, Collection<SqlScriptResourceDescriptor> dataSetDescriptors) {
        super(testEvent, dataSetDescriptors);
    }
}
