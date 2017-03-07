package org.arquillian.cube.populator.rest.postman.runner;

import java.net.MalformedURLException;
import java.net.URL;

public class HostPortOverride {

    private String host;
    private int port = -1;

    public HostPortOverride() {
    }

    public HostPortOverride(String host) {
        this.host = host;
    }

    public HostPortOverride(int port) {
        this.port = port;
    }

    public HostPortOverride(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public URL override(URL original) {
        if (host == null && port == -1) {
            // No override
            return original;
        } else {
            String newHost = host == null ? original.getHost() : this.host;
            int newPort = port == -1 ? original.getPort() : this.port;

            try {
                return new URL(original.getProtocol(), newHost, newPort, original.getFile());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }

        }
    }

}
