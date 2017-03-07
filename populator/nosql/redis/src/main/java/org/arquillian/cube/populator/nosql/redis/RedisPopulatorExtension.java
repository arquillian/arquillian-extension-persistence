package org.arquillian.cube.populator.nosql.redis;

import org.arquillian.cube.populator.nosql.api.NoSqlPopulatorEnricher;
import org.arquillian.cube.populator.spi.PopulatorService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class RedisPopulatorExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(PopulatorService.class, RedisPopulatorService.class)
                .service(ResourceProvider.class, NoSqlPopulatorEnricher.class);
    }
}
