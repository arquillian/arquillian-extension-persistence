package org.arquillian.ape.junit.rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.arquillian.ape.spi.Populator;
import org.arquillian.ape.spi.PopulatorService;

class Reflection {

    private Reflection() {
    }

    public static boolean isClassWithAnnotation(final Class<?> source,
        final Class<? extends Annotation> annotationClass) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
            boolean annotationPresent = false;
            Class<?> nextSource = source;
            while (nextSource != Object.class) {
                if (nextSource.isAnnotationPresent(annotationClass)) {
                    return true;
                }
                nextSource = nextSource.getSuperclass();
            }
            return annotationPresent;
        });
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

        final Constructor<?> serviceConstructor = serviceClass.getDeclaredConstructor();
        serviceConstructor.setAccessible(true);
        final Object service = serviceConstructor.newInstance();

        // Using getDeclaredConstructor(serviceClass) does not work. Services must contain only one constructor, if not even the Arquillian runner fails in case of APE
        final Constructor<?> populatorConstructor = populatorClass.getDeclaredConstructors()[0];
        populatorConstructor.setAccessible(true);
        final Populator populator = (Populator) populatorConstructor.newInstance(service);
        field.set(testInstance, populator);
    }

}
