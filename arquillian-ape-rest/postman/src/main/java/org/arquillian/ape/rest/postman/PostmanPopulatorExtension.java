package org.arquillian.ape.rest.postman;

import java.lang.annotation.Annotation;
import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.api.DeclarativeSupport;
import org.arquillian.ape.spi.junit.rule.JUnitRuleSupport;
import org.arquillian.ape.rest.RestPopulator;
import org.arquillian.ape.rest.RestPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class PostmanPopulatorExtension implements LoadableExtension, JUnitRuleSupport {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, PostmanPopulatorService.class)
            .service(ResourceProvider.class, RestPopulatorEnricher.class);
    }

    @Override
    public Class<? extends Annotation> populatorAnnotation() {
        return Postman.class;
    }

    @Override
    public Class<? extends PopulatorService> populatotService() {
        return PostmanPopulatorService.class;
    }

    @Override
    public Class<? extends Populator> populator() {
        return RestPopulator.class;
    }

    @Override
    public DeclarativeSupport declarativeSupport() {
        throw new UnsupportedOperationException();
    }
}
