package org.arquillian.ape.rest.postman;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.arquillian.ape.rest.RestDeclarativeSupport;
import org.arquillian.ape.rest.RestPopulator;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

public class PostmanDeclarativeSupport extends RestDeclarativeSupport {

    protected PostmanDeclarativeSupport() {
        super(new RestPopulator(new PostmanPopulatorService()));
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
        return Postman.class;
    }
}
