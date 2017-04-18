package org.arquillian.ape.rdbms.flyway;

import java.lang.annotation.Annotation;
import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.spi.junit.rule.JUnitRuleSupport;
import org.arquillian.ape.rdbms.core.RdbmsPopulator;
import org.arquillian.ape.rdbms.core.RdbmsPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class FlywayPopulatorExtension implements LoadableExtension, JUnitRuleSupport {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, FlywayPopulatorService.class)
            .service(ResourceProvider.class, RdbmsPopulatorEnricher.class);
    }

    @Override
    public Class<? extends Annotation> populatorAnnotation() {
        return Flyway.class;
    }

    @Override
    public Class<? extends PopulatorService> populatotService() {
        return FlywayPopulatorService.class;
    }

    @Override
    public Class<? extends Populator> populator() {
        return RdbmsPopulator.class;
    }
}
