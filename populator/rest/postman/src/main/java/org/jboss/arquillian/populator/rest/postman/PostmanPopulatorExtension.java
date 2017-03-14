package org.jboss.arquillian.populator.rest.postman;

import org.jboss.arquillian.populator.rest.api.RestPopulatorEnricher;
import org.jboss.arquillian.populator.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class PostmanPopulatorExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, PostmanPopulatorService.class)
                .service(ResourceProvider.class, RestPopulatorEnricher.class);
    }
}
