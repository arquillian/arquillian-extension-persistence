package org.arquillian.ape.rest.postman;

import java.util.List;
import java.util.Map;
import org.arquillian.ape.rest.RestPopulatorService;
import org.arquillian.ape.rest.postman.runner.HostPortOverride;
import org.arquillian.ape.rest.postman.runner.PostmanRunner;

public class PostmanPopulatorService implements RestPopulatorService<Postman> {

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
        postmanRunner.executeCalls(new HostPortOverride(host, bindPort), variables,
            resources.toArray(new String[resources.size()]));
    }

    private void executeScripts(List<String> resources, Map<String, String> variables) {
        PostmanRunner postmanRunner = new PostmanRunner();
        postmanRunner.executeCalls(variables, resources.toArray(new String[resources.size()]));
    }
}
