package org.arquillian.ape.rdbms.core;

import java.net.URI;
import java.util.HashMap;
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
            .connect(URI.create("jdbc:postgresql://localhost:5432/books"), "postgres", "postgres", String.class,
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
            .connect(URI.create("jdbc:postgresql://192.168.99.100:5432/conference"), "postgres", "postgres", String.class,
                new HashMap<>());
    }

}
