package org.arquillian.ape.rdbms.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import org.arquillian.ape.core.RunnerExpressionParser;

public class SpringBootLoader {

    private static final String JDBC_URI_PROPERTY_NAME = "spring.datasource.url";
    private static final String USERNAME_PROPERTY_NAME = "spring.datasource.username";
    private static final String PASSWORD_PROPERTY_NAME = "spring.datasource.password";
    private static final String DRIVER_PROPERTY_NAME = "spring.datasource.driverClassName";

    public static DatabaseConfiguration load(String location) {

        final InputStream configurationFile = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(location);

        if (configurationFile == null) {
            throw new IllegalArgumentException(String.format("Spring Boot configuration file %s not found in classpath.", location));
        }

        Properties properties = new Properties();
        try {
            properties.load(configurationFile);

            final String jdbc = properties.getProperty(JDBC_URI_PROPERTY_NAME);
            final String driverClass = properties.getProperty(DRIVER_PROPERTY_NAME);
            final String username = properties.getProperty(USERNAME_PROPERTY_NAME);
            final String password = properties.getProperty(PASSWORD_PROPERTY_NAME);
            return new DatabaseConfiguration(URI.create(RunnerExpressionParser.parseExpressions(jdbc)),
                Class.forName(RunnerExpressionParser.parseExpressions(driverClass)),
                RunnerExpressionParser.parseExpressions((username)),
                RunnerExpressionParser.parseExpressions(password));
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
