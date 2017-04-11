package org.arquillian.ape.rdbms.core.datasource;

import java.lang.annotation.Annotation;
import org.arquillian.ape.rdbms.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class PersistenceConfigurationEnricher implements ResourceProvider {

    @Inject
    private Instance<PersistenceConfiguration> configurationInstance;

    @Override
    public boolean canProvide(Class<?> type) {
        return type.isAssignableFrom(PersistenceConfiguration.class);
    }

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return configurationInstance.get();
    }
}
