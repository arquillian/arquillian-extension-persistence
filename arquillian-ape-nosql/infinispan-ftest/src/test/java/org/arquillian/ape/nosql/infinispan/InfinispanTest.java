package org.arquillian.ape.nosql.infinispan;

import java.net.MalformedURLException;
import org.arquillian.ape.nosql.NoSqlPopulator;
import org.arquillian.cube.docker.impl.client.containerobject.dsl.Container;
import org.arquillian.cube.docker.impl.client.containerobject.dsl.DockerContainer;
import org.assertj.core.api.Assertions;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class InfinispanTest {

    @Infinispan
    @ArquillianResource
    NoSqlPopulator populator;

    @DockerContainer
    Container infinispan = Container.withContainerName("infinispan")
                                  .fromImage("jboss/infinispan-server:9.0.0.Final")
                                  .withCommand("standalone")
                                  .withPortBinding(11222)
                                  .build();

    @Test
    public void should_populate_infinispan() throws MalformedURLException {
        populator.forServer(infinispan.getIpAddress(), infinispan.getBindPort(11222))
            .usingDataSet("users.json")
            .execute();

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addServer().host(infinispan.getIpAddress()).port(infinispan.getBindPort(11222));

        RemoteCacheManager remoteCacheManager = new RemoteCacheManager(configurationBuilder.build());
        final RemoteCache<Object, User> cache = remoteCacheManager.getCache();

        assertThat(cache.get("alex")).isNotNull();

    }

}
