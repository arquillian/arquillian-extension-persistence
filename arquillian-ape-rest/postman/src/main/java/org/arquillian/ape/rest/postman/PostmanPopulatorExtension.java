package org.arquillian.ape.rest.postman;

import org.arquillian.ape.rest.RestPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

class PostmanPopulatorExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, PostmanPopulatorService.class)
            .service(ResourceProvider.class, RestPopulatorEnricher.class);
    }
}
