package org.arquillian.ape.nosql.vault;

import com.bettercloud.vault.VaultException;
import java.util.Map;
import org.arquillian.ape.nosql.NoSqlPopulator;
import org.arquillian.cube.docker.impl.client.containerobject.dsl.Container;
import org.arquillian.cube.docker.impl.client.containerobject.dsl.DockerContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.arquillian.ape.nosql.vault.VaultOptions.options;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class VaultTest {

    public static final String ROOT_TOKEN = "c56a4180-65aa-42ec-a945-5fd21dec0538";
    @ArquillianResource
    @Vault
    NoSqlPopulator vaultPopulator;

    @DockerContainer
    Container vault = Container.withContainerName("vault")
                                .fromImage("vault:0.7.0")
                                .withPortBinding(8200)
                                .withEnvironment("VAULT_DEV_ROOT_TOKEN_ID", ROOT_TOKEN)
                                .build();

    @Test
    public void should_read_secrets_from_vault() throws VaultException {

        // given

        vaultPopulator.forServer("http://" + vault.getIpAddress(), vault.getBindPort(8200))
                      .usingDataSet("mysecret.yml")
                      .withOptions(options()
                                        .token(ROOT_TOKEN)
                                        .build())
                      .execute();

        // when

        final Secrets secrets = new Secrets(vault.getIpAddress(), vault.getBindPort(8200), ROOT_TOKEN);
        final Map<String, String> data = secrets.getFooSecret();

        // then

        assertThat(data).containsEntry("zip", "zap").containsEntry("a", "b");

    }


}
