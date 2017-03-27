package org.jboss.arquillian.populator.nosql.couchbase;

import org.jboss.arquillian.populator.nosql.api.NoSqlPopulatorEnricher;
import org.jboss.arquillian.populator.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

class CouchbasePopulatorExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, CouchbasePopulatorService.class)
                .service(ResourceProvider.class, NoSqlPopulatorEnricher.class);
    }
}
