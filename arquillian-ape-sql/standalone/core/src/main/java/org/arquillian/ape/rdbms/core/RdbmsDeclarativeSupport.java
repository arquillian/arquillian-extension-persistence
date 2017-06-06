package org.arquillian.ape.rdbms.core;

import java.util.Collection;
import java.util.Map;
import org.arquillian.ape.api.AbstractDeclarativeSupport;
import org.arquillian.ape.api.Authentication;
import org.arquillian.ape.api.Driver;
import org.arquillian.ape.api.MetadataExtractor;
import org.arquillian.ape.api.NoType;
import org.arquillian.ape.api.Server;
import org.arquillian.ape.api.Uri;
import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.core.RunnerExpressionParser;
import org.jboss.arquillian.test.spi.TestClass;

public abstract class RdbmsDeclarativeSupport extends AbstractDeclarativeSupport {

    private RdbmsPopulator rdbmsPopulator;
    private RdbmsPopulatorConfigurator rdbmsPopulatorConfigurator;

    protected RdbmsDeclarativeSupport(RdbmsPopulator rdbmsPopulator) {
        this.rdbmsPopulator = rdbmsPopulator;
    }

    protected void configureConnection(final Collection<Server> serverAnnotations, final Collection<Uri> uriAnnotations,
        Map<String, Object> options, TestClass testClass) {

        final RdbmsPopulatorConfigurator rdbmsPopulatorConfigurator;

        // Now we only support one type of database each time. Not possible to populate to two instances of same type of database
        if (serverAnnotations.size() == 1) {
            final Server serverAnnotation = serverAnnotations.iterator().next();

            int port = Integer.parseInt(RunnerExpressionParser.parseExpressions(serverAnnotation.port()));
            rdbmsPopulatorConfigurator =
                this.rdbmsPopulator.forServer(RunnerExpressionParser.parseExpressions(serverAnnotation.host()), port);

            this.rdbmsPopulatorConfigurator = rdbmsPopulatorConfigurator;
        } else {
            if (uriAnnotations.size() == 1) {
                final Uri uriAnnotation = uriAnnotations.iterator().next();

                rdbmsPopulatorConfigurator =
                    this.rdbmsPopulator.forUri(RunnerExpressionParser.parseExpressions(uriAnnotation.value()));

                this.rdbmsPopulatorConfigurator = rdbmsPopulatorConfigurator;
            }
        }

        if (this.rdbmsPopulatorConfigurator != null) {

            this.rdbmsPopulatorConfigurator.withOptions(options);

            final MetadataExtractor metadataExtractor = new MetadataExtractor(testClass);

            final Collection<Authentication> authenticationAnnotation = metadataExtractor.authentication()
                .getAnnotationsOnClassLevel();

            // Now we only support one type of database each time. Not possible to populate to two instances of same type of database
            if (authenticationAnnotation.size() == 1) {
                final Authentication auth = authenticationAnnotation.iterator().next();
                this.rdbmsPopulatorConfigurator.withUsername(RunnerExpressionParser.parseExpressions(auth.username()))
                    .withPassword(RunnerExpressionParser.parseExpressions(auth.password()));
            }

            final Collection<Driver> driverAnnotation = metadataExtractor.driver()
                .getAnnotationsOnClassLevel(
                    driver -> driver.type() == getPopulatorAnnotation() || driver.type() == NoType.class);

            // Now we only support one type of database each time. Not possible to populate to two instances of same type of database
            if (driverAnnotation.size() == 1) {
                final Driver driver = driverAnnotation.iterator().next();
                this.rdbmsPopulatorConfigurator.withDriver(driver.value());
            }
        }
    }

    protected void populateData(UsingDataSet usingDataSet) {

        if (rdbmsPopulatorConfigurator == null) {
            return;
        }

        final String[] value = usingDataSet.value();

        rdbmsPopulatorConfigurator.usingDataSets(value);
        rdbmsPopulatorConfigurator.execute();
    }

    protected void cleanData() {

        if (rdbmsPopulatorConfigurator == null) {
            // Programmatic approach
            return;
        }

        rdbmsPopulatorConfigurator.clean();
    }
}
