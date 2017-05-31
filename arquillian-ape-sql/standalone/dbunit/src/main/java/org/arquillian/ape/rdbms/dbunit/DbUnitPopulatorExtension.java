package org.arquillian.ape.rdbms.dbunit;

import java.lang.annotation.Annotation;
import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.api.DeclarativeSupport;
import org.arquillian.ape.spi.junit.rule.JUnitRuleSupport;
import org.arquillian.ape.rdbms.core.RdbmsPopulator;
import org.arquillian.ape.rdbms.core.RdbmsPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class DbUnitPopulatorExtension implements LoadableExtension, JUnitRuleSupport {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, DbUnitPopulatorService.class)
            .service(ResourceProvider.class, RdbmsPopulatorEnricher.class);
    }

    @Override
    public Class<? extends Annotation> populatorAnnotation() {
        return DbUnit.class;
    }

    @Override
    public Class<? extends PopulatorService> populatotService() {
        return DbUnitPopulatorService.class;
    }

    @Override
    public Class<? extends Populator> populator() {
        return RdbmsPopulator.class;
    }

    @Override
    public DeclarativeSupport declarativeSupport() {
        throw new UnsupportedOperationException();
    }
}
