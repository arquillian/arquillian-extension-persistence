package org.arquillian.ape.rdbms.flyway;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.arquillian.ape.rdbms.core.RdbmsPopulatorService;

public class FlywayPopulatorService implements RdbmsPopulatorService<Flyway> {

    private org.flywaydb.core.Flyway flyway = new org.flywaydb.core.Flyway();

    @Override
    public void connect(URI jdbc, String username, String password, Class<?> driver, Map<String, Object> customOptions) {
        this.flyway.setDataSource(jdbc.toString(),username, password);
        final FlywayOptions flywayOptions = new FlywayOptions(customOptions);
        flywayOptions.configure(this.flyway);
    }

    @Override
    public void disconnect() {
    }

    @Override
    public void execute(List<String> resources) {
        this.flyway.setLocations(resources.toArray(new String[resources.size()]));
        this.flyway.migrate();
    }

    @Override
    public void clean(List<String> resources) {
        flyway.clean();
    }

    @Override
    public Class<Flyway> getPopulatorAnnotation() {
        return Flyway.class;
    }

}
