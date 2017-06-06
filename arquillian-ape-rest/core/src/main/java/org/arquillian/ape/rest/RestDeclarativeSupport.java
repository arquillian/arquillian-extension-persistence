package org.arquillian.ape.rest;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.arquillian.ape.api.AbstractDeclarativeSupport;
import org.arquillian.ape.api.MetadataExtractor;
import org.arquillian.ape.api.NoType;
import org.arquillian.ape.api.Server;
import org.arquillian.ape.api.Uri;
import org.arquillian.ape.api.UrlOverride;
import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.api.Variable;
import org.arquillian.ape.core.RunnerExpressionParser;
import org.jboss.arquillian.test.spi.TestClass;

public abstract class RestDeclarativeSupport extends AbstractDeclarativeSupport {

    private RestPopulator restPopulator;
    protected RestPopulatorConfigurator restPopulatorConfigurator;

    protected RestDeclarativeSupport(RestPopulator restPopulator) {
        this.restPopulator = restPopulator;
    }

    protected void configureConnection(final Collection<Server> serverAnnotations, final Collection<Uri> uriAnnotations,
        Map<String, Object> options, TestClass testClass) {

        final RestPopulatorConfigurator restPopulatorConfigurator;

        // Now we only support one type of database each time. Not possible to populate to two instances of same type of database
        if (serverAnnotations.size() == 1) {
            final Server serverAnnotation = serverAnnotations.iterator().next();

            int port = Integer.parseInt(RunnerExpressionParser.parseExpressions(serverAnnotation.port()));
            restPopulatorConfigurator =
                this.restPopulator.forServer(RunnerExpressionParser.parseExpressions(serverAnnotation.host()), port);

            this.restPopulatorConfigurator = restPopulatorConfigurator;
        } else {
            if (uriAnnotations.size() == 1) {
                final Uri uriAnnotation = uriAnnotations.iterator().next();

                restPopulatorConfigurator =
                    this.restPopulator.forUri(RunnerExpressionParser.parseExpressions(uriAnnotation.value()));

                this.restPopulatorConfigurator = restPopulatorConfigurator;
            }
        }

        if (this.restPopulatorConfigurator != null) {

            final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);

            final Collection<Variable> variablesAnnotation = metadataExtractor.variable()
                .getAnnotationsOnClassLevel();
            final Map<String, String> variables =
                variablesAnnotation.stream().collect(Collectors.toMap(v -> v.name(), v -> RunnerExpressionParser.parseExpressions(v.value())));

            this.restPopulatorConfigurator.withVariables(variables);

            final Collection<UrlOverride> urlOverrides = metadataExtractor.urlOverride()
                .getAnnotationsOnClassLevel(
                    urlOverride -> urlOverride.type() == getPopulatorAnnotation() || urlOverride.type() == NoType.class);

            // Now we only support one type of database each time. Not possible to populate to two instances of same type of database
            if (urlOverrides.size() == 1) {
                final UrlOverride urlOverride = urlOverrides.iterator().next();
                if (!urlOverride.value()) {
                    this.restPopulatorConfigurator.avoidUrlOverride();
                }
            }
        }
    }

    protected void populateData(UsingDataSet usingDataSet) {

        if (restPopulatorConfigurator == null) {
            return;
        }

        final String[] value = usingDataSet.value();

        restPopulatorConfigurator.usingDataSets(value);
        restPopulatorConfigurator.execute();
    }

    protected void cleanData() {

        if (restPopulatorConfigurator == null) {
            // Programmatic approach
            return;
        }

        restPopulatorConfigurator.clean();
    }
}
