package org.jboss.arquillian.populator.rest.postman.runner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.fail;

public class PostmanRunnerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void should_send_get_methods() {
        // given

        wireMockRule.stubFor(
                get(urlEqualTo("/"))
                .willReturn(aResponse().withBody("Hello World"))
        );

        // when
        PostmanRunner postmanRunner = new PostmanRunner();
        postmanRunner.executeCalls(new HashMap<>(), "/SimpleRequest.json");

        // then
        verify(getRequestedFor(urlEqualTo("/")));
    }

    @Test
    public void should_execute_post_with_url_override() {
        // given
        wireMockRule
                .stubFor(
                post(urlEqualTo("/?a=a"))
                        .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                        .withRequestBody(equalToJson("{\"name\": \"test\"}"))
                        .willReturn(aResponse().withStatus(200))
        );

        // when
        PostmanRunner postmanRunner = new PostmanRunner();

        HostPortOverride hostPortOverride = new HostPortOverride("localhost", 8080);
        try {
            postmanRunner.executeCalls(hostPortOverride, new HashMap<>(), "/ComplexPostRequestWithBody.json");
        } catch(Exception e) {
            fail(wireMockRule.findNearMissesForAllUnmatchedRequests().toString());
        }

        // then
        verify(postRequestedFor(urlEqualTo("/?a=a"))
                .withRequestBody(equalToJson("{\"name\": \"test\"}")));

    }

}
