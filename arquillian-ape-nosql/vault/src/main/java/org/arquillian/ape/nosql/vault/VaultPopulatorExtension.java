package org.arquillian.ape.nosql.vault;

import java.lang.annotation.Annotation;
import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.api.DeclarativeSupport;
import org.arquillian.ape.spi.junit.rule.JUnitRuleSupport;
import org.arquillian.ape.nosql.NoSqlPopulator;
import org.arquillian.ape.nosql.NoSqlPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class VaultPopulatorExtension implements LoadableExtension, JUnitRuleSupport {

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, VaultPopulatorService.class)
            .service(ResourceProvider.class, NoSqlPopulatorEnricher.class)
            .observer(VaultDeclarativeSupport.class);
    }

    @Override
    public Class<? extends Annotation> populatorAnnotation() {
        return Vault.class;
    }

    @Override
    public Class<? extends PopulatorService> populatotService() {
        return VaultPopulatorService.class;
    }

    @Override
    public Class<? extends Populator> populator() {
        return NoSqlPopulator.class;
    }

    @Override
    public DeclarativeSupport declarativeSupport() {
        return new VaultDeclarativeSupport();
    }
}
