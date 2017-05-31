package org.arquillian.ape.nosql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.arquillian.ape.api.Cleanup;
import org.arquillian.ape.api.DeclarativeSupport;
import org.arquillian.ape.api.MetadataExtractor;
import org.arquillian.ape.api.NoType;
import org.arquillian.ape.api.Server;
import org.arquillian.ape.api.TestExecutionPhase;
import org.arquillian.ape.api.Uri;
import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.core.RunnerExpressionParser;
import org.jboss.arquillian.test.spi.TestClass;

public abstract class NoSqlDeclarativeSupport implements DeclarativeSupport {

    private NoSqlPopulator noSqlPopulator;
    private NoSqlPopulatorConfigurator noSqlPopulatorConfigurator;

    protected NoSqlDeclarativeSupport(NoSqlPopulator noSqlPopulator) {
        this.noSqlPopulator = noSqlPopulator;
    }

    protected abstract Map<String, Object> getConfiguration(TestClass testClass);
    protected abstract Class<? extends Annotation> getPopulatorAnnotation();

    public void clean(TestClass testClass, Method testMethod, boolean isTestExecuted) {
        if (noSqlPopulatorConfigurator == null) {
            // Programmatic approach
            return;
        }

        final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);

        final Collection<Cleanup> cleanups = metadataExtractor.cleanup()
            .fetchUsingFirst(testMethod,
                cleanup -> cleanup.type() == getPopulatorAnnotation() || cleanup.type() == NoType.class);

        if (cleanups.size() == 1) {
            Cleanup cleanupAnnotation = cleanups.iterator().next();

            TestExecutionPhase testExecutionPhase = isTestExecuted ? TestExecutionPhase.AFTER : TestExecutionPhase.BEFORE;

            if (testExecutionPhase == cleanupAnnotation.phase()) {
                cleanData(noSqlPopulatorConfigurator);
            } else {
                if (TestExecutionPhase.AFTER == testExecutionPhase && TestExecutionPhase.DEFAULT == cleanupAnnotation.phase()) {
                    cleanData(noSqlPopulatorConfigurator);
                }
            }
        }

    }

    public void configure(TestClass testClass) {

        Map<String, Object> options = getConfiguration(testClass);

        final Optional<NoSqlPopulatorConfigurator> noSqlPopulatorConfigurator =
            configureConnection(testClass, options);

        noSqlPopulatorConfigurator.ifPresent(configuration -> this.noSqlPopulatorConfigurator = configuration);
    }

    public void populate(TestClass testClass, Method testMethod) {

        final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);

        final Collection<UsingDataSet> usingDataSetAnnotation =
            metadataExtractor.usingDataSet().fetchUsingFirst(testMethod, server -> server.type() == getPopulatorAnnotation() || server.type() == NoType.class);

        // Now we only support one type of database each time. Not possible to populate to two instances of same type of database

        if (usingDataSetAnnotation.size() == 1) {
            if (noSqlPopulatorConfigurator == null) {
                throw new IllegalArgumentException(
                    String.format("No server location provided in class %s either using %s or %s annotations.",
                        testClass.getName(), Server.class.getName(), Uri.class.getName()));
            }
            populateData(noSqlPopulatorConfigurator, usingDataSetAnnotation.iterator().next());
        }
    }

    protected Optional<NoSqlPopulatorConfigurator> configureConnection(TestClass testClass, Map<String, Object> options) {

        final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);

        final Collection<Server> serverAnnotations =
            metadataExtractor.server().getAnnotationsOnClassLevel(server -> server.type() == getPopulatorAnnotation() || server.type() == NoType.class);

        final NoSqlPopulatorConfigurator noSqlPopulatorConfigurator;

        // Now we only support one type of database each time. Not possible to populate to two instances of same type of database
        if (serverAnnotations.size() == 1) {
            final Server serverAnnotation = serverAnnotations.iterator().next();

            int port = Integer.parseInt(RunnerExpressionParser.parseExpressions(serverAnnotation.port()));
            noSqlPopulatorConfigurator =
                this.noSqlPopulator.forServer(RunnerExpressionParser.parseExpressions(serverAnnotation.host()), port);
            noSqlPopulatorConfigurator.withStorage(RunnerExpressionParser.parseExpressions(serverAnnotation.storage()));
            noSqlPopulatorConfigurator.withOptions(options);

            return Optional.of(noSqlPopulatorConfigurator);
        } else {

            final Collection<Uri> uriAnnotations = metadataExtractor.uri()
                .getAnnotationsOnClassLevel(uri -> uri.type() == getPopulatorAnnotation() || uri.type() == NoType.class);

            if (uriAnnotations.size() == 1) {
                final Uri uriAnnotation = uriAnnotations.iterator().next();

                noSqlPopulatorConfigurator =
                    this.noSqlPopulator.forUri(RunnerExpressionParser.parseExpressions(uriAnnotation.value()));
                noSqlPopulatorConfigurator.withStorage(RunnerExpressionParser.parseExpressions(uriAnnotation.storage()));
                noSqlPopulatorConfigurator.withOptions(options);

                return Optional.of(noSqlPopulatorConfigurator);
            }
        }

        return Optional.empty();
    }

    protected void populateData(NoSqlPopulatorConfigurator noSqlPopulatorConfigurator, UsingDataSet usingDataSet) {
        final String[] value = usingDataSet.value();

        noSqlPopulatorConfigurator.usingDataSets(value);
        noSqlPopulatorConfigurator.execute();
    }

    protected void cleanData(NoSqlPopulatorConfigurator noSqlPopulatorConfigurator) {
        noSqlPopulatorConfigurator.clean();
    }
}
