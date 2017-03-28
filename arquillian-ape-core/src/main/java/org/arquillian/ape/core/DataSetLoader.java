package org.arquillian.ape.core;

import java.io.InputStream;

public class DataSetLoader {

    private DataSetLoader() {
    }

    public static InputStream resolve(String location) {
        if (!location.startsWith("/")) {
            location = "/" + location;
        }

        return DataSetLoader.class.getResourceAsStream(location);

    }

}
