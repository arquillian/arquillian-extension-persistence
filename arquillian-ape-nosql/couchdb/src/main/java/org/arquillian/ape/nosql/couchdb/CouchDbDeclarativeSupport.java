package org.arquillian.ape.nosql.couchdb;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.arquillian.ape.api.MetadataExtractor;
import org.arquillian.ape.nosql.NoSqlDeclarativeSupport;
import org.arquillian.ape.nosql.NoSqlPopulator;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

public class CouchDbDeclarativeSupport extends NoSqlDeclarativeSupport {

    protected CouchDbDeclarativeSupport() {
        super(new NoSqlPopulator(new CouchDbPopulatorService()));
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
        final Optional<CouchDbConfiguration> redisConfigurationOptional =
            Optional.ofNullable(metadataExtractor.using(CouchDbConfiguration.class).getAnnotationOnClassLevel());

        final Map<String, Object> options = new HashMap<>();
        redisConfigurationOptional.ifPresent(redisConfiguration -> options.putAll(CouchDbOptions.from(redisConfiguration)));

        return options;
    }

    @Override
    protected Class<? extends Annotation> getPopulatorAnnotation() {
        return CouchDb.class;
    }
}
