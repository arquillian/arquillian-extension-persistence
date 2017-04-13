package org.arquillian.ape.rdbms.core.enricher;

import java.lang.annotation.Annotation;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class ScriptingConfigurationEnricher implements ResourceProvider {

    @Inject
    private Instance<ScriptingConfiguration> scriptingConfigurationInstance;

    public boolean canProvide(Class<?> type) {
        return type.isAssignableFrom(ScriptingConfiguration.class);
    }

    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return scriptingConfigurationInstance.get();
    }
}
