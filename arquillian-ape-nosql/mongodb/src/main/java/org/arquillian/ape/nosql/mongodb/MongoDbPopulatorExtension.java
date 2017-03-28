package org.arquillian.ape.nosql.mongodb;

import org.arquillian.ape.nosql.NoSqlPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

class MongoDbPopulatorExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, MongoDbPopulatorService.class)
                .service(ResourceProvider.class, NoSqlPopulatorEnricher.class);
    }
}
