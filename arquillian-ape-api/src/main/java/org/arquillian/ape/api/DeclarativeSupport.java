package org.arquillian.ape.api;

import java.lang.reflect.Method;
import org.jboss.arquillian.test.spi.TestClass;

public interface DeclarativeSupport {

    void configure(TestClass testClass);
    void populate(TestClass testClass, Method testMethod);
    void clean(TestClass testClass, Method testMethod, boolean isTestExecuted);

}
