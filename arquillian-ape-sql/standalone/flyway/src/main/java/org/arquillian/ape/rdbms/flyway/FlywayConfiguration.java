package org.arquillian.ape.rdbms.flyway;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to configure Flyway connection.
 * All attributes can be set by system property or environment variable by using:
 *
 * ${property:default_value}
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface FlywayConfiguration {

    String installedBy() default "";
    String allowMixedMigrations() default "";
    String ignoreMissingMigrations() default "";
    String ignoreFutureMigrations() default "";
    String ignoreFailedFutureMigrations() default "";
    String validateOnMigrate() default "";
    String cleanOnValidationError() default "";
    String encoding() default "";
    String[] schemas() default {};
    String table() default "";
    String target() default "";
    String placeholderReplacement() default "";
    String placeholderSuffix() default "";
    String placeholderPrefix() default "";
    String sqlMigrationPrefix() default "";
    String repeatableSqlMigrationPrefix() default "";
    String sqlMigrationSeparator() default "";
    String sqlMigrationSuffix() default "";
    String baselineVersion() default "";
    String baselineDescription() default "";
    String baselineOnMigrate() default "";
    String outOfOrder() default "";
    String[] callbacks() default {};
    String skipDefaultCallback() default "";
    String[] resolvers() default {};
    String skipDefaultResolvers() default "";
    Placeholder[] placeholders() default {};
}
