package org.jboss.arquillian.persistence.dbunit.enricher;

import java.lang.annotation.Annotation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class DBUnitConfigurationEnricher implements ResourceProvider {

    @Inject
    private Instance<DBUnitConfiguration> dbUnitConfigurationInstance;

    public boolean canProvide(Class<?> type) {
        return type.isAssignableFrom(DBUnitConfiguration.class);
    }

    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return dbUnitConfigurationInstance.get();
    }
}
