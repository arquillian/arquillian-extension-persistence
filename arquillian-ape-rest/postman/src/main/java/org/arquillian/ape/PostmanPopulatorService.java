package org.jboss.arquillian.populator.rest.postman;

import org.jboss.arquillian.populator.rest.api.RestPopulatorService;
import org.jboss.arquillian.populator.rest.postman.runner.HostPortOverride;
import org.jboss.arquillian.populator.rest.postman.runner.PostmanRunner;

import java.util.List;
import java.util.Map;

class PostmanPopulatorService implements RestPopulatorService<Postman> {

    @Override
    public Class<Postman> getPopulatorAnnotation() {
        return Postman.class;
    }

    @Override
    public void execute(List<String> resources, Map<String, String> variables) {
        executeScripts(resources, variables);
    }

    @Override
    public void execute(String host, int bindPort, List<String> resources, Map<String, String> variables) {
        executeScripts(host, bindPort, resources, variables);
    }

    @Override
    public void clean(List<String> resources, Map<String, String> variables) {
        executeScripts(resources, variables);
    }

    @Override
    public void clean(String host, int bindPort, List<String> resources, Map<String, String> variables) {
        executeScripts(host, bindPort, resources, variables);
    }

    private void executeScripts(String host, int bindPort, List<String> resources, Map<String, String> variables) {
        PostmanRunner postmanRunner = new PostmanRunner();
        postmanRunner.executeCalls(new HostPortOverride(host, bindPort), variables, resources.toArray(new String[resources.size()]));
    }

    private void executeScripts(List<String> resources, Map<String, String> variables) {
        PostmanRunner postmanRunner = new PostmanRunner();
        postmanRunner.executeCalls(variables, resources.toArray(new String[resources.size()]));
    }
}
