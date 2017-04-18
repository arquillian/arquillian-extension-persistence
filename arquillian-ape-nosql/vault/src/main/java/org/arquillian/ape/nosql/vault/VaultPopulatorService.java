package org.arquillian.ape.nosql.vault;

import com.bettercloud.vault.VaultConfig;
import com.lordofthejars.nosqlunit.vault.DefaultVaultInsertionStrategy;
import com.lordofthejars.nosqlunit.vault.VaultClientCallback;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.arquillian.ape.core.DataSetLoader;
import org.arquillian.ape.nosql.NoSqlPopulatorService;

class VaultPopulatorService implements NoSqlPopulatorService<Vault>  {

    private VaultConfig vaultConfig;

    @Override
    public void connect(String host, int bindPort, String database, Map<String, Object> customOptions) {
        this.vaultConfig = new VaultConfig();
        vaultConfig.address(host + ":" + bindPort);
        VaultOptions vaultOptions = new VaultOptions(customOptions);
        vaultOptions.configure(vaultConfig);
    }

    @Override
    public void connect(URI uri, String database, Map<String, Object> customOptions) {
        this.vaultConfig = new VaultConfig();
        vaultConfig.address(uri.toString());
        VaultOptions vaultOptions = new VaultOptions(customOptions);
        vaultOptions.configure(vaultConfig);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void execute(List<String> resources) {
        DefaultVaultInsertionStrategy vaultInsertionStrategy = new DefaultVaultInsertionStrategy();
        VaultClientCallback vaultClientCallback = () -> vaultConfig;

        resources.stream()
            .map(DataSetLoader::resolve)
            .forEach(dataset -> {
                try {
                    vaultInsertionStrategy.insert(vaultClientCallback, dataset);
                } catch (Throwable throwable) {
                    throw new IllegalStateException(throwable);
                }
            });
    }

    @Override
    public void clean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<Vault> getPopulatorAnnotation() {
        return Vault.class;
    }
}
