package org.arquillian.persistence.dbunit.configuration;

public class DBUnitConfigurationDefinitionException extends RuntimeException {

    private static final long serialVersionUID = -8266901423293560757L;

    public DBUnitConfigurationDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBUnitConfigurationDefinitionException(String message) {
        super(message);
    }

    public DBUnitConfigurationDefinitionException(Throwable cause) {
        super(cause);
    }

}
