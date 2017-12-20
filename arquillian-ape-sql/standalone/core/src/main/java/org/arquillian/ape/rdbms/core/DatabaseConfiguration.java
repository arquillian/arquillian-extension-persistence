package org.arquillian.ape.rdbms.core;

import java.net.URI;

class DatabaseConfiguration {

    private URI jdbc;
    private Class<?> jdbcDriver;
    private String username;
    private String password;

    DatabaseConfiguration() {
    }

    DatabaseConfiguration(URI jdbc, Class<?> jdbcDriver, String username, String password) {
        this.jdbc = jdbc;
        this.jdbcDriver = jdbcDriver;
        this.username = username;
        this.password = password;
    }

    void setJdbc(URI jdbc) {
        this.jdbc = jdbc;
    }

    void setJdbcDriver(Class<?> jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setPassword(String password) {
        this.password = password;
    }

    URI getJdbc() {
        return jdbc;
    }

    Class<?> getJdbcDriver() {
        return jdbcDriver;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }
}
