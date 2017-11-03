package org.arquillian.ape.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import org.jboss.arquillian.test.spi.TestClass;

public abstract class AbstractDeclarativeSupport implements DeclarativeSupport {

    public void configure(TestClass testClass) {

        Map<String, Object> options = getConfiguration(testClass);

        final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);

        final Collection<Server> serverAnnotations =
            metadataExtractor.server()
                .getAnnotationsOnClassLevel(
                    server -> server.type() == getPopulatorAnnotation() || server.type() == NoType.class);

        final Collection<Uri> uriAnnotations = metadataExtractor.uri()
            .getAnnotationsOnClassLevel(uri -> uri.type() == getPopulatorAnnotation() || uri.type() == NoType.class);

        configureConnection(serverAnnotations, uriAnnotations, options, testClass);
    }

    public void clean(TestClass testClass, Method testMethod, boolean isTestExecuted) {

        final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);

        final Collection<Cleanup> cleanups = metadataExtractor.cleanup()
            .fetchUsingFirst(testMethod,
                cleanup -> cleanup.type() == getPopulatorAnnotation() || cleanup.type() == NoType.class);

        if (cleanups.size() == 1) {
            Cleanup cleanupAnnotation = cleanups.iterator().next();

            TestExecutionPhase testExecutionPhase = isTestExecuted ? TestExecutionPhase.AFTER : TestExecutionPhase.BEFORE;

            if (testExecutionPhase == cleanupAnnotation.phase()) {
                cleanData();
            } else {
                if (TestExecutionPhase.AFTER == testExecutionPhase
                    && TestExecutionPhase.DEFAULT == cleanupAnnotation.phase()) {
                    cleanData();
                }
            }
        }
    }

    public void populate(TestClass testClass, Method testMethod) {

        final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);

        final Collection<UsingDataSet> usingDataSetAnnotation =
            metadataExtractor.usingDataSet()
                .fetchUsingFirst(testMethod,
                    usingDataSet -> usingDataSet.type() == getPopulatorAnnotation() || usingDataSet.type() == NoType.class);

        // Now we only support one type of database each time. Not possible to populate to two instances of same type of database

        if (usingDataSetAnnotation.size() == 1) {
            populateData(usingDataSetAnnotation.iterator().next());
        }
    }

    protected abstract void configureConnection(final Collection<Server> serverAnnotations,
        final Collection<Uri> uriAnnotations, Map<String, Object> options, TestClass testClass);

    protected abstract void populateData(UsingDataSet usingDataSet);

    protected abstract void cleanData();

    protected abstract Map<String, Object> getConfiguration(TestClass testClass);

    protected abstract Class<? extends Annotation> getPopulatorAnnotation();
}
