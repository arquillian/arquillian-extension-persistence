package org.arquillian.ape.nosql;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.jboss.arquillian.test.spi.TestClass;

public class MockedNoSqlDeclarativeSupport extends NoSqlDeclarativeSupport {

    private Class<? extends Annotation> type;

    public MockedNoSqlDeclarativeSupport(NoSqlPopulator noSqlPopulator, Class<? extends Annotation> type) {
        super(noSqlPopulator);
        this.type = type;
    }

    @Override
    protected Map<String, Object> getConfiguration(TestClass testClass) {
        return new HashMap<>();
    }

    @Override
    protected Class<? extends Annotation> getPopulatorAnnotation() {
        return type;
    }
}
