package org.arquillian.ape.nosql.couchdb;

import java.lang.annotation.Annotation;
import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.spi.junit.rule.JUnitRuleSupport;
import org.arquillian.ape.nosql.NoSqlPopulator;
import org.arquillian.ape.nosql.NoSqlPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

class CouchDbPopulatorExtension implements LoadableExtension, JUnitRuleSupport {

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, CouchDbPopulatorService.class)
            .service(ResourceProvider.class, NoSqlPopulatorEnricher.class);
    }

    @Override
    public Class<? extends Annotation> populatorAnnotation() {
        return CouchDb.class;
    }

    @Override
    public Class<? extends PopulatorService> populatotService() {
        return CouchDbPopulatorService.class;
    }

    @Override
    public Class<? extends Populator> populator() {
        return NoSqlPopulator.class;
    }
}
