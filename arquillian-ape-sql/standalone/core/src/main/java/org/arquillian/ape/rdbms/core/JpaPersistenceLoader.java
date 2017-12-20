package org.arquillian.ape.rdbms.core;

import java.io.InputStream;
import java.net.URI;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.arquillian.ape.core.RunnerExpressionParser;

public class JpaPersistenceLoader {

    private static final String JDBC_URI_PROPERTY_NAME = "javax.persistence.jdbc.url";
    private static final String USERNAME_PROPERTY_NAME = "javax.persistence.jdbc.user";
    private static final String PASSWORD_PROPERTY_NAME = "javax.persistence.jdbc.password";
    private static final String DRIVER_PROPERTY_NAME = "javax.persistence.jdbc.driver";

    public static DatabaseConfiguration load(String location) {
        final InputStream persistenceContent = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);

        if (persistenceContent == null) {
            throw new IllegalArgumentException(String.format("Persistence file %s not found in classpath.", location));
        }
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            final XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(persistenceContent);
            while(xmlEventReader.hasNext()) {
                final XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    final StartElement startElement = xmlEvent.asStartElement();
                    if ("property".equalsIgnoreCase(startElement.getName().getLocalPart())) {
                        final Attribute name = startElement.getAttributeByName(new QName("name"));
                        if (name != null) {
                            switch (name.getValue()) {
                                case JDBC_URI_PROPERTY_NAME: databaseConfiguration.setJdbc(URI.create(extractValue(startElement)));
                                break;
                                case USERNAME_PROPERTY_NAME: databaseConfiguration.setUsername(extractValue(startElement));
                                break;
                                case PASSWORD_PROPERTY_NAME: databaseConfiguration.setPassword(extractValue(startElement));
                                break;
                                case DRIVER_PROPERTY_NAME: databaseConfiguration.setJdbcDriver(Class.forName(extractValue(startElement)));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (XMLStreamException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

        return databaseConfiguration;
    }

    private static String extractValue(StartElement startElement) {
        return RunnerExpressionParser.parseExpressions(startElement.getAttributeByName(new QName("value")).getValue());
    }
}
