package org.arquillian.ape.rest.postman.runner.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompleteUrl {

    private String protocol;

    private Domain domain;

    private Path path;

    private String port;
    private String hash;

    private List<QueryParam> query = new ArrayList<>();
    private List<Variable> variable = new ArrayList<>();

    public URL getAsUrl() throws MalformedURLException {

        int realPort = port == null ? 80 : Integer.parseInt(port);
        final Map<String, Object> variablesAsMap = getVariablesAsMap();

        StringBuilder file = new StringBuilder();

        final String fullPath = path.getPath().stream()
                .map(path -> {
                    if (path.startsWith(":")) {
                        return variablesAsMap.get(path.substring(1)).toString();
                    } else {
                        return path;
                    }

                })
                .map(URLEncoder::encode)
                .collect(Collectors.joining("/"));

        if (! fullPath.startsWith("/")) {
            file.append("/");
        }

        file.append(fullPath);

        if (! query.isEmpty()) {
            final String fullQuery = query.stream()
                    .map(QueryParam::asString)
                    .collect(Collectors.joining("&"));
            file.append("?").append(fullQuery);
        }

        if (hash != null && ! hash.isEmpty()) {
            file.append("#").append(hash);
        }

        return new URL(protocol, domain.getDomain(), realPort, file.toString());
    }

    private Map<String, Object> getVariablesAsMap() {
        return variable.stream()
                .collect(Collectors.toMap(Variable::getId, Variable::getValue));
    }

}
