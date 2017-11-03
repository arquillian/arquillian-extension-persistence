package org.arquillian.ape.rdbms.flyway;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.arquillian.ape.core.RunnerExpressionParser;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.flywaydb.core.api.resolver.MigrationResolver;

public class FlywayOptions implements Map<String, Object> {

    final static String INSTALLED_BY = "installedBy";
    final static String ALLOW_MIXED_MIGRATIONS = "allowMixedMigrations";
    final static String IGNORE_MISSING_MIGRATIONS = "ignoreMissingMigrations";
    final static String IGNORE_FUTURE_MIGRATIONS = "ignoreFutureMigrations";
    final static String IGNORE_FAILED_FUTURE_MIGRATIONS = "ignoreFailedFutureMigrations";
    final static String VALIDATE_ON_MIGRATE = "validateOnMigrate";
    final static String CLEAN_ON_VALIDATION_ERROR = "cleanOnValidationError";
    final static String ENCODING = "encoding";
    final static String SCHEMAS = "schemas";
    final static String TABLE = "table";
    final static String TARGET = "target";
    final static String PLACEHOLDER_REPLACEMENT = "placeholderReplacement";
    final static String PLACEHOLDERS = "placeholders";
    final static String PLACEHOLDER_SUFFIX = "placeholderSuffix";
    final static String PLACEHOLDER_PREFIX = "placeholderPrefix";
    final static String SQL_MIGRATION_PREFIX = "sqlMigrationPrefix";
    final static String REPEATABLE_SQL_MIGRATION_PREFIX = "repeatableSqlMigrationPrefix";
    final static String SQL_MIGRATION_SEPARATOR = "sqlMigrationSeparator";
    final static String SQL_MIGRATION_SUFFIX = "sqlMigrationSuffix";
    final static String BASELINE_VERSION = "baselineVersion";
    final static String BASELINE_DESCRIPTION = "baselineDescription";
    final static String BASELINE_ON_MIGRATE = "baselineOnMigrate";
    final static String OUT_OF_ORDER = "outOfOrder";
    final static String CALLBACKS = "callbacks";
    final static String CALLBACKS_STRING = "callbacksString";
    final static String SKIP_DEFAULT_CALLBACK = "skipDefaultCallback";
    final static String RESOLVERS = "resolvers";
    final static String RESOLVERS_STRING = "resolversString";
    final static String SKIP_DEFAULT_RESOLVERS = "skipDefaultResolvers";

    private Map<String, Object> options = new HashMap<>();

    private FlywayOptions() {
    }

    FlywayOptions(Map<String, Object> options) {
        this.options.putAll(options);
    }

    public static FlywayConfigurationOptions options() {
        return new FlywayConfigurationOptions();
    }

    @Override
    public int size() {
        return options.size();
    }

    @Override
    public boolean isEmpty() {
        return options.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return options.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return options.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return options.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return options.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return options.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        options.putAll(m);
    }

    @Override
    public void clear() {
        options.clear();
    }

    @Override
    public Set<String> keySet() {
        return options.keySet();
    }

    @Override
    public Collection<Object> values() {
        return options.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return options.entrySet();
    }

    void configure(org.flywaydb.core.Flyway flyway) {
        if (this.options.containsKey(INSTALLED_BY)) {
            flyway.setInstalledBy((String) this.options.get(INSTALLED_BY));
        }

        if (this.options.containsKey(ALLOW_MIXED_MIGRATIONS)) {
            flyway.setAllowMixedMigrations((Boolean) this.options.get(ALLOW_MIXED_MIGRATIONS));
        }

        if (this.options.containsKey(IGNORE_MISSING_MIGRATIONS)) {
            flyway.setIgnoreMissingMigrations((Boolean) this.options.get(IGNORE_MISSING_MIGRATIONS));
        }

        if (this.options.containsKey(IGNORE_FUTURE_MIGRATIONS)) {
            flyway.setIgnoreFutureMigrations((Boolean) this.options.get(IGNORE_FUTURE_MIGRATIONS));
        }

        if (this.options.containsKey(IGNORE_FAILED_FUTURE_MIGRATIONS)) {
            flyway.setIgnoreFailedFutureMigration((Boolean) this.options.get(IGNORE_FAILED_FUTURE_MIGRATIONS));
        }

        if (this.options.containsKey(VALIDATE_ON_MIGRATE)) {
            flyway.setValidateOnMigrate((Boolean) this.options.get(VALIDATE_ON_MIGRATE));
        }

        if (this.options.containsKey(CLEAN_ON_VALIDATION_ERROR)) {
            flyway.setCleanOnValidationError((Boolean) this.options.get(CLEAN_ON_VALIDATION_ERROR));
        }

        if (this.options.containsKey(ENCODING)) {
            flyway.setEncoding((String) this.options.get(ENCODING));
        }

        if (this.options.containsKey(SCHEMAS)) {
            flyway.setSchemas((String[]) this.options.get(SCHEMAS));
        }

        if (this.options.containsKey(TABLE)) {
            flyway.setTable((String) this.options.get(TABLE));
        }

        if (this.options.containsKey(TARGET)) {
            flyway.setTargetAsString((String) this.options.get(TARGET));
        }

        if (this.options.containsKey(PLACEHOLDER_REPLACEMENT)) {
            flyway.setPlaceholderReplacement((Boolean) this.options.get(PLACEHOLDER_REPLACEMENT));
        }

        if (this.options.containsKey(PLACEHOLDERS)) {
            flyway.setPlaceholders((Map<String, String>) this.options.get(PLACEHOLDERS));
        }

        if (this.options.containsKey(PLACEHOLDER_SUFFIX)) {
            flyway.setPlaceholderSuffix((String) this.options.get(PLACEHOLDER_SUFFIX));
        }

        if (this.options.containsKey(PLACEHOLDER_PREFIX)) {
            flyway.setPlaceholderPrefix((String) this.options.get(PLACEHOLDER_PREFIX));
        }

        if (this.options.containsKey(SQL_MIGRATION_PREFIX)) {
            flyway.setSqlMigrationPrefix((String) this.options.get(SQL_MIGRATION_PREFIX));
        }

        if (this.options.containsKey(REPEATABLE_SQL_MIGRATION_PREFIX)) {
            flyway.setRepeatableSqlMigrationPrefix((String) this.options.get(REPEATABLE_SQL_MIGRATION_PREFIX));
        }

        if (this.options.containsKey(SQL_MIGRATION_SEPARATOR)) {
            flyway.setSqlMigrationSeparator((String) this.options.get(SQL_MIGRATION_SEPARATOR));
        }

        if (this.options.containsKey(BASELINE_VERSION)) {
            flyway.setBaselineVersionAsString((String) this.options.get(BASELINE_VERSION));
        }

        if (this.options.containsKey(BASELINE_DESCRIPTION)) {
            flyway.setBaselineDescription((String) this.options.get(BASELINE_DESCRIPTION));
        }

        if (this.options.containsKey(SQL_MIGRATION_SUFFIX)) {
            flyway.setSqlMigrationSuffix((String) this.options.get(SQL_MIGRATION_SUFFIX));
        }

        if (this.options.containsKey(BASELINE_ON_MIGRATE)) {
            flyway.setBaselineOnMigrate((Boolean) this.options.get(BASELINE_ON_MIGRATE));
        }

        if (this.options.containsKey(OUT_OF_ORDER)) {
            flyway.setOutOfOrder((Boolean) this.options.get(OUT_OF_ORDER));
        }

        if (this.options.containsKey(SKIP_DEFAULT_CALLBACK)) {
            flyway.setSkipDefaultCallbacks((Boolean) this.options.get(SKIP_DEFAULT_CALLBACK));
        }

        if (this.options.containsKey(SKIP_DEFAULT_RESOLVERS)) {
            flyway.setSkipDefaultResolvers((Boolean) this.options.get(SKIP_DEFAULT_RESOLVERS));
        }

        if (this.options.containsKey(CALLBACKS)) {
            flyway.setCallbacks((FlywayCallback[]) this.options.get(CALLBACKS));
        }

        if (this.options.containsKey(CALLBACKS_STRING)) {
            flyway.setCallbacksAsClassNames((String[]) this.options.get(CALLBACKS_STRING));
        }

        if (this.options.containsKey(RESOLVERS)) {
            flyway.setResolvers((MigrationResolver[]) this.options.get(RESOLVERS));
        }

        if (this.options.containsKey(RESOLVERS_STRING)) {
            flyway.setResolversAsClassNames((String[]) this.options.get(RESOLVERS_STRING));
        }
    }

    public static FlywayOptions from(FlywayConfiguration flywayConfiguration) {
        final Map<String, Object> options = new HashMap<>();

        if (!flywayConfiguration.installedBy().isEmpty()) {
            options.put(INSTALLED_BY, RunnerExpressionParser.parseExpressions(flywayConfiguration.installedBy()));
        }

        if (!flywayConfiguration.allowMixedMigrations().isEmpty()) {
            options.put(ALLOW_MIXED_MIGRATIONS, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.allowMixedMigrations())));
        }

        if (!flywayConfiguration.ignoreMissingMigrations().isEmpty()) {
            options.put(IGNORE_MISSING_MIGRATIONS, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.ignoreMissingMigrations())));
        }

        if (!flywayConfiguration.ignoreFutureMigrations().isEmpty()) {
            options.put(IGNORE_FUTURE_MIGRATIONS, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.ignoreFutureMigrations())));
        }

        if (!flywayConfiguration.ignoreFailedFutureMigrations().isEmpty()) {
            options.put(IGNORE_FAILED_FUTURE_MIGRATIONS, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.ignoreFailedFutureMigrations())));
        }

        if (!flywayConfiguration.validateOnMigrate().isEmpty()) {
            options.put(VALIDATE_ON_MIGRATE, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.validateOnMigrate())));
        }

        if (!flywayConfiguration.cleanOnValidationError().isEmpty()) {
            options.put(CLEAN_ON_VALIDATION_ERROR, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.cleanOnValidationError())));
        }

        if (!flywayConfiguration.encoding().isEmpty()) {
            options.put(ENCODING, RunnerExpressionParser.parseExpressions(flywayConfiguration.encoding()));
        }

        if (flywayConfiguration.schemas().length > 0) {
            List<String> resolved = Arrays.stream(flywayConfiguration.schemas())
                .map(RunnerExpressionParser::parseExpressions)
                .collect(Collectors.toList());

            options.put(SCHEMAS, resolved.toArray(new String[resolved.size()]));
        }

        if (!flywayConfiguration.table().isEmpty()) {
            options.put(TABLE, RunnerExpressionParser.parseExpressions(flywayConfiguration.table()));
        }

        if (!flywayConfiguration.target().isEmpty()) {
            options.put(TARGET, RunnerExpressionParser.parseExpressions(flywayConfiguration.target()));
        }

        if(flywayConfiguration.placeholders().length > 0) {
            final Map<String, String> placeholders = Arrays.stream(flywayConfiguration.placeholders())
                .collect(Collectors.toMap(p -> p.key(), p -> RunnerExpressionParser.parseExpressions(p.value())));

            options.put(PLACEHOLDERS, placeholders);
        }

        if (!flywayConfiguration.placeholderReplacement().isEmpty()) {
            options.put(PLACEHOLDER_REPLACEMENT, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.placeholderReplacement())));
        }

        if (!flywayConfiguration.placeholderSuffix().isEmpty()) {
            options.put(PLACEHOLDER_SUFFIX,
                RunnerExpressionParser.parseExpressions(flywayConfiguration.placeholderSuffix()));
        }

        if (!flywayConfiguration.placeholderPrefix().isEmpty()) {
            options.put(PLACEHOLDER_PREFIX,
                RunnerExpressionParser.parseExpressions(flywayConfiguration.placeholderPrefix()));
        }

        if (!flywayConfiguration.sqlMigrationPrefix().isEmpty()) {
            options.put(SQL_MIGRATION_PREFIX,
                RunnerExpressionParser.parseExpressions(flywayConfiguration.sqlMigrationPrefix()));
        }

        if (!flywayConfiguration.repeatableSqlMigrationPrefix().isEmpty()) {
            options.put(REPEATABLE_SQL_MIGRATION_PREFIX,
                RunnerExpressionParser.parseExpressions(flywayConfiguration.repeatableSqlMigrationPrefix()));
        }

        if (!flywayConfiguration.sqlMigrationSeparator().isEmpty()) {
            options.put(SQL_MIGRATION_SEPARATOR,
                RunnerExpressionParser.parseExpressions(flywayConfiguration.sqlMigrationSeparator()));
        }

        if (!flywayConfiguration.sqlMigrationSuffix().isEmpty()) {
            options.put(SQL_MIGRATION_SUFFIX,
                RunnerExpressionParser.parseExpressions(flywayConfiguration.sqlMigrationSuffix()));
        }

        if (!flywayConfiguration.baselineVersion().isEmpty()) {
            options.put(BASELINE_VERSION, RunnerExpressionParser.parseExpressions(flywayConfiguration.baselineVersion()));
        }

        if (!flywayConfiguration.baselineDescription().isEmpty()) {
            options.put(BASELINE_DESCRIPTION,
                RunnerExpressionParser.parseExpressions(flywayConfiguration.baselineDescription()));
        }

        if (!flywayConfiguration.baselineOnMigrate().isEmpty()) {
            options.put(BASELINE_ON_MIGRATE,
                RunnerExpressionParser.parseExpressions(flywayConfiguration.baselineOnMigrate()));
        }

        if (!flywayConfiguration.outOfOrder().isEmpty()) {
            options.put(OUT_OF_ORDER, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.outOfOrder())));
        }

        if (flywayConfiguration.callbacks().length > 0) {
            List<String> callbacks = Arrays.stream(flywayConfiguration.callbacks())
                .map(RunnerExpressionParser::parseExpressions)
                .collect(Collectors.toList());

            options.put(CALLBACKS_STRING, callbacks.toArray(new String[callbacks.size()]));
        }

        if (!flywayConfiguration.skipDefaultCallback().isEmpty()) {
            options.put(SKIP_DEFAULT_CALLBACK, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.skipDefaultCallback())));
        }

        if (flywayConfiguration.resolvers().length > 0) {
            List<String> resolvers = Arrays.stream(flywayConfiguration.resolvers())
                .map(RunnerExpressionParser::parseExpressions)
                .collect(Collectors.toList());

            options.put(RESOLVERS_STRING, resolvers.toArray(new String[resolvers.size()]));
        }

        if (!flywayConfiguration.skipDefaultResolvers().isEmpty()) {
            options.put(SKIP_DEFAULT_RESOLVERS, Boolean.parseBoolean(
                RunnerExpressionParser.parseExpressions(flywayConfiguration.skipDefaultResolvers())));
        }

        return new FlywayOptions(options);
    }

    public static class FlywayConfigurationOptions {
        private FlywayOptions flywayOptions = new FlywayOptions();

        private FlywayConfigurationOptions() {
        }

        public FlywayConfigurationOptions installedBy(String installedBy) {
            flywayOptions.put(FlywayOptions.INSTALLED_BY, installedBy);
            return this;
        }

        public FlywayConfigurationOptions allowMixedMigrations(Boolean allowMixedMigrations) {
            flywayOptions.put(FlywayOptions.ALLOW_MIXED_MIGRATIONS, allowMixedMigrations);
            return this;
        }

        public FlywayConfigurationOptions ignoreMissingMigrations(Boolean ignoreMissingMigrations) {
            flywayOptions.put(FlywayOptions.IGNORE_MISSING_MIGRATIONS, ignoreMissingMigrations);
            return this;
        }

        public FlywayConfigurationOptions ignoreFutureMigrations(Boolean ignoreFutureMigrations) {
            flywayOptions.put(FlywayOptions.IGNORE_FUTURE_MIGRATIONS, ignoreFutureMigrations);
            return this;
        }

        public FlywayConfigurationOptions ignoreFailedFutureMigrations(Boolean ignoreFailedFutureMigrations) {
            flywayOptions.put(FlywayOptions.IGNORE_FAILED_FUTURE_MIGRATIONS, ignoreFailedFutureMigrations);
            return this;
        }

        public FlywayConfigurationOptions validateOnMigrate(Boolean validateOnMigrate) {
            flywayOptions.put(FlywayOptions.VALIDATE_ON_MIGRATE, validateOnMigrate);
            return this;
        }

        public FlywayConfigurationOptions cleanOnValidationError(Boolean cleanOnValidationError) {
            flywayOptions.put(FlywayOptions.CLEAN_ON_VALIDATION_ERROR, cleanOnValidationError);
            return this;
        }

        public FlywayConfigurationOptions encoding(String encoding) {
            flywayOptions.put(FlywayOptions.ENCODING, encoding);
            return this;
        }

        public FlywayConfigurationOptions schemas(String... schemas) {
            flywayOptions.put(FlywayOptions.SCHEMAS, schemas);
            return this;
        }

        public FlywayConfigurationOptions table(String table) {
            flywayOptions.put(FlywayOptions.TABLE, table);
            return this;
        }

        public FlywayConfigurationOptions target(String target) {
            flywayOptions.put(FlywayOptions.TARGET, target);
            return this;
        }

        public FlywayConfigurationOptions placeholderReplacement(Boolean placeholderReplacement) {
            flywayOptions.put(FlywayOptions.PLACEHOLDER_REPLACEMENT, placeholderReplacement);
            return this;
        }

        public FlywayConfigurationOptions placeholders(Map<String, String> placeholders) {
            flywayOptions.put(FlywayOptions.PLACEHOLDERS, placeholders);
            return this;
        }

        public FlywayConfigurationOptions placeholderSuffix(String placeholderSuffix) {
            flywayOptions.put(FlywayOptions.PLACEHOLDER_SUFFIX, placeholderSuffix);
            return this;
        }

        public FlywayConfigurationOptions placeholderPrefix(String placeholderPrefix) {
            flywayOptions.put(FlywayOptions.PLACEHOLDER_PREFIX, placeholderPrefix);
            return this;
        }

        public FlywayConfigurationOptions sqlMigrationPrefix(String sqlMigrationPrefix) {
            flywayOptions.put(FlywayOptions.SQL_MIGRATION_PREFIX, sqlMigrationPrefix);
            return this;
        }

        public FlywayConfigurationOptions repeatableSqlMigrationPrefix(String repeatableSqlMigrationPrefix) {
            flywayOptions.put(FlywayOptions.REPEATABLE_SQL_MIGRATION_PREFIX, repeatableSqlMigrationPrefix);
            return this;
        }

        public FlywayConfigurationOptions sqlMigrationSeparator(String sqlMigrationSeparator) {
            flywayOptions.put(FlywayOptions.SQL_MIGRATION_SEPARATOR, sqlMigrationSeparator);
            return this;
        }

        public FlywayConfigurationOptions baselineVersion(String baselineVersion) {
            flywayOptions.put(FlywayOptions.BASELINE_VERSION, baselineVersion);
            return this;
        }

        public FlywayConfigurationOptions baselineDescription(String baselineDescription) {
            flywayOptions.put(FlywayOptions.BASELINE_DESCRIPTION, baselineDescription);
            return this;
        }

        public FlywayConfigurationOptions sqlMigrationSuffix(String sqlMigrationSuffix) {
            flywayOptions.put(FlywayOptions.SQL_MIGRATION_SUFFIX, sqlMigrationSuffix);
            return this;
        }

        public FlywayConfigurationOptions baselineOnMigrate(Boolean baselineOnMigrate) {
            flywayOptions.put(FlywayOptions.BASELINE_ON_MIGRATE, baselineOnMigrate);
            return this;
        }

        public FlywayConfigurationOptions outOfOrder(Boolean outOfOrder) {
            flywayOptions.put(FlywayOptions.OUT_OF_ORDER, outOfOrder);
            return this;
        }

        public FlywayConfigurationOptions callbacks(FlywayCallback... callbacks) {
            flywayOptions.put(FlywayOptions.CALLBACKS, callbacks);
            return this;
        }

        public FlywayConfigurationOptions resolvers(MigrationResolver... resolvers) {
            flywayOptions.put(FlywayOptions.RESOLVERS, resolvers);
            return this;
        }

        public FlywayConfigurationOptions skipDefaultCallback(Boolean skipDefaultCallback) {
            flywayOptions.put(FlywayOptions.SKIP_DEFAULT_CALLBACK, skipDefaultCallback);
            return this;
        }

        public FlywayConfigurationOptions skipDefaultResolvers(Boolean skipDefaultResolvers) {
            flywayOptions.put(FlywayOptions.SKIP_DEFAULT_RESOLVERS, skipDefaultResolvers);
            return this;
        }

        public FlywayOptions build() {
            return flywayOptions;
        }
    }
}
