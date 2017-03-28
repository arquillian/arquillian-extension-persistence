package org.arquillian.ape.core;

import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

/**
 * Base enricher that populator enrichers can extend
 *
 * @param <T>
 */
public abstract class PopulatorEnricher<T extends PopulatorService> implements ResourceProvider {

    @Inject
    Instance<ServiceLoader> serviceLoaderInstance;

    @Inject
    Instance<Injector> injectorInstance;

    @Override
    public boolean canProvide(Class<?> type) {
        return Populator.class.isAssignableFrom(type);
    }

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {

        final Optional<Annotation> populatorAnnotation = getPopulatorAnnotation(qualifiers);

        if (!populatorAnnotation.isPresent()) {
            throw new IllegalArgumentException("Element is annotated with Arquillian Resource of type Populator, but there is no annotation meta-annotated witt @Populator for choosing implementation.");
        }

        final Optional<T> populatorService = findPopulatorService(populatorAnnotation.get());

        if (!populatorAnnotation.isPresent()) {
            throw new IllegalArgumentException(String.format("No Populator Service found for annotation %s.", populatorAnnotation.get().annotationType()));
        }

        Populator populator = createPopulator(populatorService.get());
        return injectorInstance.get().inject(populator);
    }

    /**
     * Method that instantiate the custom populator DSL specific for each case. For example NoSql populator, Sql populator, ...
     *
     * @param populatorService used in DSL Populator to execute and clean methods.
     * @return Populator instance.
     */
    public abstract Populator createPopulator(T populatorService);

    /**
     * Method that finds which PopulatorService is in charge of populating data for given annotation.
     *
     * @param annotation that specifies the populator service.
     * @return First PopulatorService registered with given annotation.
     */
    private Optional<T> findPopulatorService(Annotation annotation) {
        Class<T> typeOf = (Class<T>)
                ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0];
        return serviceLoaderInstance.get()
                .all(PopulatorService.class).stream()
                .filter(populatorService -> populatorService.getPopulatorAnnotation().equals(annotation.annotationType()))
                .map(typeOf::cast)
                .findFirst();
    }

    /**
     * Method that finds which annotation is meta-annotated with org.jboss.arquillian.populator.api.Populator.
     * In case of multiple annotations found, the first one is returned.
     *
     * @param qualifiers present in Arquillian Resource
     * @return Annotation found meta-annotated with org.jboss.arquillian.populator.api.Populator
     * @see org.arquillian.ape.api.Populator
     */
    private Optional<Annotation> getPopulatorAnnotation(Annotation... qualifiers) {
        return Arrays.stream(qualifiers)
                .filter(annotation -> annotation.annotationType().getAnnotationsByType(org.arquillian.ape.api.Populator.class) != null)
                .findFirst();
    }

}
