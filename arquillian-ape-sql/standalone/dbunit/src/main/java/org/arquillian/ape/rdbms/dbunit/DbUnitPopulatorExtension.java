package org.arquillian.ape.rdbms.dbunit;

import org.arquillian.ape.rdbms.core.RdbmsPopulatorEnricher;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class DbUnitPopulatorExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, DbUnitPopulatorService.class)
            .service(ResourceProvider.class, RdbmsPopulatorEnricher.class);
    }
}
