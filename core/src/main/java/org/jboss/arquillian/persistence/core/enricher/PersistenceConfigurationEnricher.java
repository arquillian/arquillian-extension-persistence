package org.jboss.arquillian.persistence.core.enricher;

import java.lang.annotation.Annotation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class PersistenceConfigurationEnricher implements ResourceProvider {

    @Inject
    private Instance<PersistenceConfiguration> persistenceConfigurationInstance;

    public boolean canProvide(Class<?> type) {
        return type.isAssignableFrom(PersistenceConfiguration.class);
    }

    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return persistenceConfigurationInstance.get();
    }
}
