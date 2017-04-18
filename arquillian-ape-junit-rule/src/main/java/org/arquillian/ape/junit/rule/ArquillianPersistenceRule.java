package org.arquillian.ape.junit.rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.spi.PopulatorService;
import org.arquillian.ape.spi.junit.rule.JUnitRuleSupport;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ArquillianPersistenceRule implements MethodRule {

    private final static Map<Class<? extends Annotation>, PopulatorInfo> populators = new HashMap<>();

    static {

        ServiceLoader<JUnitRuleSupport> serviceLoader = ServiceLoader.load(JUnitRuleSupport.class);
        StreamSupport.stream(serviceLoader.spliterator(), false)
            .forEach(service -> {
            populators.put(service.populatorAnnotation(), new PopulatorInfo(service.populatotService(), service.populator()));
        });

    }

    static class PopulatorInfo {
        Class<? extends PopulatorService> populatorService;
        Class<? extends Populator> populator;

        PopulatorInfo(Class<? extends PopulatorService> populatorService,
            Class<? extends Populator> populator) {
            this.populatorService = populatorService;
            this.populator = populator;
        }
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final List<Field> allFieldsAnnotatedWith =
                    Reflection.getAllFieldsAnnotatedWith(target.getClass(), ArquillianResource.class);

                final Set<Map.Entry<Class<? extends Annotation>, PopulatorInfo>> entries = populators.entrySet();

                for (Map.Entry<Class<? extends Annotation>, PopulatorInfo> serviceEntry : entries) {
                    final Optional<Field> fieldAnnotedWithPopulatorAnnotation =
                        Reflection.getFieldAnnotedWith(allFieldsAnnotatedWith, serviceEntry.getKey());

                    if (fieldAnnotedWithPopulatorAnnotation.isPresent()) {
                        Reflection.instantiateServiceAndPopulatorAndInject(target,
                            fieldAnnotedWithPopulatorAnnotation.get(),
                            serviceEntry.getValue().populatorService, serviceEntry.getValue().populator);
                    }
                }

                base.evaluate();
            }
        };
    }



}
