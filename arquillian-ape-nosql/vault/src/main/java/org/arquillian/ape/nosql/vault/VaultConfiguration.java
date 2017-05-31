package org.arquillian.ape.nosql.vault;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to configure Vault connection.
 * All attributes can be set by system property or environment variable by using:
 *
 * ${property:default_value}
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface VaultConfiguration {

    String token();
    String sslPemUtf8() default "";
    String sslPemFile() default "";
    String sslPemResource() default "";
    String sslVerify() default "";
    String openTimeout() default "";
    String readTimeout() default "";

}
