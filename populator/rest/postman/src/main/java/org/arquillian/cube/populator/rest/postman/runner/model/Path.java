package org.arquillian.cube.populator.rest.postman.runner.model;

import java.util.Arrays;
import java.util.List;

public class Path {

    private String path;
    private List<String> paths;

    public Path(String path) {
        this.path = path;
    }

    public Path(List<String> path) {
        this.paths = path;
    }

    public List<String> getPath() {
        if (path != null) {
            return Arrays.asList(path.split("/"));
        } else {
            return this.paths;
        }
    }

}
