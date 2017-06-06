package org.arquillian.ape.nosql;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.arquillian.ape.api.AbstractDeclarativeSupport;
import org.arquillian.ape.api.MetadataExtractor;
import org.arquillian.ape.api.NoType;
import org.arquillian.ape.api.Server;
import org.arquillian.ape.api.Uri;
import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.core.RunnerExpressionParser;
import org.jboss.arquillian.test.spi.TestClass;

public abstract class NoSqlDeclarativeSupport extends AbstractDeclarativeSupport {

    private NoSqlPopulator noSqlPopulator;
    private NoSqlPopulatorConfigurator noSqlPopulatorConfigurator;

    protected NoSqlDeclarativeSupport(NoSqlPopulator noSqlPopulator) {
        this.noSqlPopulator = noSqlPopulator;
    }

    protected void configureConnection(final Collection<Server> serverAnnotations, final Collection<Uri> uriAnnotations, Map<String, Object> options, TestClass testClass) {

        final NoSqlPopulatorConfigurator noSqlPopulatorConfigurator;

        // Now we only support one type of database each time. Not possible to populate to two instances of same type of database
        if (serverAnnotations.size() == 1) {
            final Server serverAnnotation = serverAnnotations.iterator().next();

            int port = Integer.parseInt(RunnerExpressionParser.parseExpressions(serverAnnotation.port()));
            noSqlPopulatorConfigurator =
                this.noSqlPopulator.forServer(RunnerExpressionParser.parseExpressions(serverAnnotation.host()), port);
            noSqlPopulatorConfigurator.withStorage(RunnerExpressionParser.parseExpressions(serverAnnotation.storage()));
            noSqlPopulatorConfigurator.withOptions(options);

            this.noSqlPopulatorConfigurator = noSqlPopulatorConfigurator;
        } else {

            if (uriAnnotations.size() == 1) {
                final Uri uriAnnotation = uriAnnotations.iterator().next();

                noSqlPopulatorConfigurator =
                    this.noSqlPopulator.forUri(RunnerExpressionParser.parseExpressions(uriAnnotation.value()));
                noSqlPopulatorConfigurator.withStorage(RunnerExpressionParser.parseExpressions(uriAnnotation.storage()));
                noSqlPopulatorConfigurator.withOptions(options);

                this.noSqlPopulatorConfigurator = noSqlPopulatorConfigurator;
            }
        }

    }

    protected void populateData(UsingDataSet usingDataSet) {

        if (noSqlPopulatorConfigurator == null) {
            return;
        }

        final String[] value = usingDataSet.value();

        noSqlPopulatorConfigurator.usingDataSets(value);
        noSqlPopulatorConfigurator.execute();
    }

    protected void cleanData() {

        if (noSqlPopulatorConfigurator == null) {
            // Programmatic approach
            return;
        }

        noSqlPopulatorConfigurator.clean();
    }
}
