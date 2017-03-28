package org.jboss.arquillian.populator.sql.core;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.populator.nosql.api.SqlPopulatorEnricher;
import org.jboss.arquillian.populator.spi.PopulatorService;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class SqlPopulatorExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, SqlPopulatorService.class)
                .service(ResourceProvider.class, SqlPopulatorEnricher.class);
    }
}
