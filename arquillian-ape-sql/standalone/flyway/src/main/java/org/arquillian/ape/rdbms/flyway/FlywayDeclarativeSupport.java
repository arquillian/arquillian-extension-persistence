package org.arquillian.ape.rdbms.flyway;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.arquillian.ape.api.MetadataExtractor;
import org.arquillian.ape.rdbms.core.RdbmsDeclarativeSupport;
import org.arquillian.ape.rdbms.core.RdbmsPopulator;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

public class FlywayDeclarativeSupport extends RdbmsDeclarativeSupport {

    protected FlywayDeclarativeSupport() {
        super(new RdbmsPopulator(new FlywayPopulatorService()));
    }

    void connect(@Observes BeforeClass beforeClass) {
        this.configure(beforeClass.getTestClass());
    }

    void populate(@Observes Before before) {
        populate(before.getTestClass(), before.getTestMethod());
    }

    // Clean should be executed before insert
    void cleanBefore(@Observes(precedence = 10) Before before) {
        this.clean(before.getTestClass(), before.getTestMethod(), false);
    }

    void cleanAfter(@Observes After after) {
        this.clean(after.getTestClass(), after.getTestMethod(), true);
    }


    @Override
    protected Map<String, Object> getConfiguration(TestClass testClass) {

        final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);
        final Optional<FlywayConfiguration> redisConfigurationOptional =
            Optional.ofNullable(metadataExtractor.using(FlywayConfiguration.class).getAnnotationOnClassLevel());

        final Map<String, Object> options = new HashMap<>();
        redisConfigurationOptional.ifPresent(redisConfiguration -> options.putAll(FlywayOptions.from(redisConfiguration)));

        return options;
    }

    @Override
    protected Class<? extends Annotation> getPopulatorAnnotation() {
        return Flyway.class;
    }
}
