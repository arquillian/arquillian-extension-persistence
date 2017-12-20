package org.arquillian.ape.rdbms.core;

import java.net.URI;
import java.util.HashMap;
import org.h2.Driver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class RdbmsPopulatorConfiguratorTest {

    @Mock
    RdbmsPopulatorService rdbmsPopulatorService;

    @Test
    public void should_load_spring_boot_properties_from_default_location() {

        // given
        RdbmsPopulatorConfigurator rdbmsPopulatorConfigurator =
            new RdbmsPopulatorConfigurator(null, rdbmsPopulatorService);

        // when
        rdbmsPopulatorConfigurator.fromSpringBootConfiguration().execute();

        // then
        Mockito.verify(rdbmsPopulatorService, times(1))
            .connect(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"), "sa", "sa", Driver.class,
                new HashMap<>());
    }

    @Test
    public void should_load_jpa_persistence_from_default_location() {

        // given
        RdbmsPopulatorConfigurator rdbmsPopulatorConfigurator =
            new RdbmsPopulatorConfigurator(null, rdbmsPopulatorService);

        // when
        rdbmsPopulatorConfigurator.fromJpaPersistence().execute();

        // then
        Mockito.verify(rdbmsPopulatorService, times(1))
            .connect(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"), "sa", "sa", Driver.class,
                new HashMap<>());
    }

    @Test
    public void should_load_wildfly_swarm_from_default_location() {

        // given
        RdbmsPopulatorConfigurator rdbmsPopulatorConfigurator =
            new RdbmsPopulatorConfigurator(null, rdbmsPopulatorService);

        // when
        rdbmsPopulatorConfigurator.fromWildflySwarmConfiguration().execute();

        // then
        Mockito.verify(rdbmsPopulatorService, times(1))
            .connect(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"), "sa", "sa", org.h2.Driver.class,
                new HashMap<>());
    }

    @Test
    public void should_load_wildfly_swarm_from_default_location_and_concrete_name() {

        // given
        RdbmsPopulatorConfigurator rdbmsPopulatorConfigurator =
            new RdbmsPopulatorConfigurator(null, rdbmsPopulatorService);

        // when
        rdbmsPopulatorConfigurator.fromWildflySwarmConfiguration("MyDS").execute();

        // then
        Mockito.verify(rdbmsPopulatorService, times(1))
            .connect(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"), "sa", "sa", org.h2.Driver.class,
                new HashMap<>());
    }

    @Test
    public void should_load_wildfly_swarm_from_specific_location_and_autoresolution_of_driver() {

        // given
        RdbmsPopulatorConfigurator rdbmsPopulatorConfigurator =
            new RdbmsPopulatorConfigurator(null, rdbmsPopulatorService);

        // when
        rdbmsPopulatorConfigurator.fromWildflySwarmConfiguration("MyDS", "custom-project-defaults.yml").execute();

        // then
        Mockito.verify(rdbmsPopulatorService, times(1))
            .connect(URI.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"), "sa", "sa",
                org.h2.Driver.class,
                new HashMap<>());
    }

}
