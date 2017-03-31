package org.arquillian.ape.rdbms.core.exception;

public class PersistenceDescriptorParsingException extends RuntimeException {

    private static final long serialVersionUID = 2151624192316744823L;

    public PersistenceDescriptorParsingException() {
    }

    public PersistenceDescriptorParsingException(String message) {
        super(message);
    }

    public PersistenceDescriptorParsingException(Throwable cause) {
        super(cause);
    }

    public PersistenceDescriptorParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
