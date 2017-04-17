package org.arquillian.ape.junit.rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.arquillian.ape.core.Populator;
import org.arquillian.ape.nosql.NoSqlPopulator;
import org.arquillian.ape.nosql.couchbase.Couchbase;
import org.arquillian.ape.nosql.couchbase.CouchbasePopulatorService;
import org.arquillian.ape.nosql.mongodb.MongoDb;
import org.arquillian.ape.nosql.mongodb.MongoDbPopulatorService;
import org.arquillian.ape.nosql.redis.Redis;
import org.arquillian.ape.nosql.redis.RedisPopulatorService;
import org.arquillian.ape.nosql.vault.Vault;
import org.arquillian.ape.nosql.vault.VaultPopulatorService;
import org.arquillian.ape.rdbms.core.RdbmsPopulator;
import org.arquillian.ape.rdbms.dbunit.DbUnit;
import org.arquillian.ape.rdbms.dbunit.DbUnitPopulatorService;
import org.arquillian.ape.rdbms.flyway.Flyway;
import org.arquillian.ape.rdbms.flyway.FlywayPopulatorService;
import org.arquillian.ape.rest.RestPopulator;
import org.arquillian.ape.rest.postman.Postman;
import org.arquillian.ape.rest.postman.PostmanPopulatorService;
import org.arquillian.ape.spi.PopulatorService;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ArquillianPersistenceRule implements MethodRule {

    private final static Map<Class<? extends Annotation>, Class<? extends PopulatorService>> services = new HashMap<>();
    private final static Map<Class<? extends PopulatorService>, Class<? extends Populator>> populators = new HashMap<>();

    static {

        try {
            services.put(MongoDb.class, MongoDbPopulatorService.class);
            populators.put(MongoDbPopulatorService.class, NoSqlPopulator.class);
        } catch (NoClassDefFoundError e) {
            // Case of class not found exception
        }

        try {
            services.put(Couchbase.class, CouchbasePopulatorService.class);
            populators.put(CouchbasePopulatorService.class, NoSqlPopulator.class);
        } catch (NoClassDefFoundError e) {
            // Case of class not found exception
        }

        try {
            services.put(Redis.class, RedisPopulatorService.class);
            populators.put(RedisPopulatorService.class, NoSqlPopulator.class);
        } catch (NoClassDefFoundError e) {
            // Case of class not found exception
        }

        try {
            services.put(Vault.class, VaultPopulatorService.class);
            populators.put(VaultPopulatorService.class, NoSqlPopulator.class);
        } catch (NoClassDefFoundError e) {
            // Case of class not found exception
        }

        try {
            services.put(Postman.class, PostmanPopulatorService.class);
            populators.put(PostmanPopulatorService.class, RestPopulator.class);
        } catch (NoClassDefFoundError e) {
            // Case of class not found exception
        }

        try {
            services.put(DbUnit.class, DbUnitPopulatorService.class);
            populators.put(DbUnitPopulatorService.class, RdbmsPopulator.class);
        } catch (NoClassDefFoundError e) {
            // Case of class not found exception
        }

        try {
            services.put(Flyway.class, FlywayPopulatorService.class);
            populators.put(FlywayPopulatorService.class, RdbmsPopulator.class);
        } catch (NoClassDefFoundError e) {
            // Case of class not found exception
        }
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final List<Field> allFieldsAnnotatedWith =
                    Reflection.getAllFieldsAnnotatedWith(target.getClass(), ArquillianResource.class);

                final Set<Map.Entry<Class<? extends Annotation>, Class<? extends PopulatorService>>> entries = services.entrySet();

                for (Map.Entry<Class<? extends Annotation>, Class<? extends PopulatorService>> serviceEntry : entries) {
                    final Optional<Field> fieldAnnotedWithPopulatorAnnotation =
                        Reflection.getFieldAnnotedWith(allFieldsAnnotatedWith, serviceEntry.getKey());

                    if (fieldAnnotedWithPopulatorAnnotation.isPresent()) {
                        Reflection.instantiateServiceAndPopulatorAndInject(target,
                            fieldAnnotedWithPopulatorAnnotation.get(),
                            serviceEntry.getValue(), populators.get(serviceEntry.getValue()));
                    }
                }

                base.evaluate();
            }
        };
    }
}
