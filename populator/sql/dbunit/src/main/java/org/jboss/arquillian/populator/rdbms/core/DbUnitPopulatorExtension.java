package org.jboss.arquillian.populator.rdbms.core;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.populator.rdbms.api.RdbmsPopulatorEnricher;
import org.jboss.arquillian.populator.spi.PopulatorService;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class DbUnitPopulatorExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, DbUnitPopulatorService.class)
                .service(ResourceProvider.class, RdbmsPopulatorEnricher.class);
    }
}
