package org.arquillian.ape.junit.rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;
import org.arquillian.ape.api.Cleanup;
import org.arquillian.ape.api.TestExecutionPhase;
import org.arquillian.ape.api.DeclarativeSupport;
import org.arquillian.ape.spi.junit.rule.JUnitRuleSupport;
import org.jboss.arquillian.test.spi.TestClass;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class DeclarativeArquillianPersistenceRule implements MethodRule {

    private final static Map<Class<? extends Annotation>, DeclarativeSupport>
        populators = new HashMap<>();

    static {

        ServiceLoader<JUnitRuleSupport> serviceLoader = ServiceLoader.load(JUnitRuleSupport.class);
        StreamSupport.stream(serviceLoader.spliterator(), false)
            .forEach(service -> {
                populators.put(service.populatorAnnotation(), service.declarativeSupport());
            });
    }


    @Override
    public Statement apply(Statement base, final FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                final TestClass testClass = new TestClass(target.getClass());
                run(declarativeSupport -> declarativeSupport.configure(testClass));

                run(declarativeSupport -> declarativeSupport.clean(testClass, method.getMethod(), false));
                run(declarativeSupport -> declarativeSupport.populate(testClass, method.getMethod()));

                base.evaluate();

                run(declarativeSupport -> declarativeSupport.clean(testClass, method.getMethod(), true));

            }

            private void run(Consumer<DeclarativeSupport> consumer) {
                final Collection<DeclarativeSupport> values = populators.values();

                for (DeclarativeSupport declarativeSupport : values) {
                    consumer.accept(declarativeSupport);
                }

            }

        };
    }
}
