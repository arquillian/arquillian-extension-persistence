package org.arquillian.ape.rest.postman.runner.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Domain {

    private String domain;
    private List<String> domainList = new ArrayList<>();

    public Domain(String domain) {
        this.domain = domain;
    }

    public Domain(List<String> domainList) {
        this.domainList.addAll(domainList);
    }

    public String getDomain() {

        if (domain == null) {
            return domainList.stream()
                    .collect(Collectors.joining("."));
        } else {
            return domain;
        }
    }
}
