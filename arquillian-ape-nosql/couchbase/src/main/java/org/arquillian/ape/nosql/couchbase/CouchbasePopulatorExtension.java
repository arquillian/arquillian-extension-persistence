package org.arquillian.ape.nosql.couchbase;

import org.arquillian.ape.nosql.NoSqlPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

class CouchbasePopulatorExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, CouchbasePopulatorService.class)
            .service(ResourceProvider.class, NoSqlPopulatorEnricher.class);
    }
}
