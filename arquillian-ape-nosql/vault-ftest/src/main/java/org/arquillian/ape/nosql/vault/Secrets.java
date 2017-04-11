package org.arquillian.ape.nosql.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import java.util.Map;

public class Secrets {

    private com.bettercloud.vault.VaultConfig vaultConfig;
    private Vault vault;

    public Secrets(String host, int port, String token) throws VaultException {
        this.vaultConfig = new VaultConfig(String.format("http://%s:%s", host, port), token);
        this.vault = new Vault(this.vaultConfig);
    }

    public Map<String, String> getFooSecret() throws VaultException {
        return this.vault.logical().read("secret/foo").getData();
    }

}
