package org.arquillian.ape.nosql.mongodb;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.arquillian.ape.nosql.NoSqlDeclarativeSupport;
import org.arquillian.ape.nosql.NoSqlPopulator;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

public class MongoDbDeclarativeSupport extends NoSqlDeclarativeSupport {

    protected MongoDbDeclarativeSupport() {
        super(new NoSqlPopulator(new MongoDbPopulatorService()));
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
        return new HashMap<>();
    }

    @Override
    protected Class<? extends Annotation> getPopulatorAnnotation() {
        return MongoDb.class;
    }
}
