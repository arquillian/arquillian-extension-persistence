package org.arquillian.ape.junit.rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.arquillian.ape.core.Populator;
import org.arquillian.ape.rdbms.dbunit.DbUnitPopulatorService;
import org.arquillian.ape.spi.PopulatorService;

public class Reflection {

    private Reflection() {
    }

    public static final List<Field> getAllFieldsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        final List<Field> fields = new ArrayList<>();

        Class<?> current = clazz;
        while (current.getSuperclass() != null) {

            fields.addAll(Arrays.stream(current.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.isAnnotationPresent(annotation))
                .collect(Collectors.toList()));

            current = current.getSuperclass();
        }

        return fields;
    }

    public static final Optional<Field> getFieldAnnotedWith(List<Field> fields, Class<? extends Annotation> annotation) {
        return fields.stream()
            .filter(field -> field.isAnnotationPresent(annotation))
            .findFirst();
    }

    public static final void instantiateServiceAndPopulatorAndInject(Object testInstance, Field field, Class<? extends PopulatorService> serviceClass, Class<? extends Populator> populatorClass)
        throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final Object service = serviceClass.newInstance();

        // Using getDeclaredConstructor(serviceClass) does not work. Services must contain only one constructor, if not even the Arquillian runner fails in case of APE
        // so it is not a constraint in this case at all.
        final Constructor<?> populatorConstructor = populatorClass.getDeclaredConstructors()[0];
        final Populator populator = (Populator) populatorConstructor.newInstance(service);
        field.set(testInstance, populator);
    }

}
