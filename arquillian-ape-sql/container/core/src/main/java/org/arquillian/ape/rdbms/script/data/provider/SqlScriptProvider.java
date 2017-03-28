/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.ape.rdbms.script.data.provider;

import org.arquillian.ape.rdbms.ApplyScriptAfter;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.arquillian.ape.rdbms.CleanupUsingScript;
import org.arquillian.ape.rdbms.CreateSchema;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.core.data.naming.FileNamingStrategy;
import org.arquillian.ape.rdbms.core.data.provider.ResourceProvider;
import org.arquillian.ape.rdbms.core.metadata.MetadataExtractor;
import org.arquillian.ape.rdbms.core.metadata.ValueExtractor;
import org.arquillian.ape.rdbms.script.ScriptLoader;
import org.arquillian.ape.rdbms.script.configuration.ScriptingConfiguration;
import org.arquillian.ape.rdbms.script.data.descriptor.FileSqlScriptResourceDescriptor;
import org.arquillian.ape.rdbms.script.data.descriptor.InlineSqlScriptResourceDescriptor;
import org.arquillian.ape.rdbms.script.data.descriptor.SqlScriptResourceDescriptor;
import org.arquillian.ape.rdbms.script.data.naming.PrefixedScriptFileNamingStrategy;
import org.jboss.arquillian.test.spi.TestClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class SqlScriptProvider<T extends Annotation> extends ResourceProvider<SqlScriptResourceDescriptor> {

    private final ScriptingConfiguration configuration;

    private final FileNamingStrategy<String> strategy;

    private final Class<T> annotation;

    private final ValueExtractor<T> extractor;

    SqlScriptProvider(Class<T> annotation, MetadataExtractor metadataExtractor, ValueExtractor<T> extractor, FileNamingStrategy<String> scriptFileNamingStrategy, ScriptingConfiguration configuration) {
        super(annotation, metadataExtractor);
        this.configuration = configuration;
        this.strategy = scriptFileNamingStrategy;
        this.annotation = annotation;
        this.extractor = extractor;
    }

    public static <K extends Annotation> SqlScriptProviderBuilder<K> forAnnotation(Class<K> annotation) {
        return SqlScriptProviderBuilder.create(annotation);
    }

    public static SqlScriptProvider<CleanupUsingScript> createProviderForCleanupScripts(TestClass testClass, ScriptingConfiguration configuration) {
        return SqlScriptProvider.forAnnotation(CleanupUsingScript.class)
                .usingConfiguration(configuration)
                .extractingMetadataUsing(new MetadataExtractor(testClass))
                .namingFollows(new PrefixedScriptFileNamingStrategy("cleanup-", "sql"))
                .build(new ValueExtractor<CleanupUsingScript>() {
                    @Override
                    public String[] extract(CleanupUsingScript toExtract) {
                        return toExtract.value();
                    }

                    @Override
                    public boolean shouldExtract(CleanupUsingScript toExtract) {
                        return (toExtract != null && !TestExecutionPhase.NONE.equals(toExtract.phase()));
                    }
                });
    }

    public static SqlScriptProvider<ApplyScriptAfter> createProviderForScriptsToBeAppliedAfterTest(TestClass testClass, ScriptingConfiguration configuration) {
        return SqlScriptProvider.forAnnotation(ApplyScriptAfter.class)
                .usingConfiguration(configuration)
                .extractingMetadataUsing(new MetadataExtractor(testClass))
                .namingFollows(new PrefixedScriptFileNamingStrategy("after-", "sql"))
                .build(new ValueExtractor<ApplyScriptAfter>() {
                    @Override
                    public String[] extract(ApplyScriptAfter toExtract) {
                        return toExtract.value();
                    }

                    @Override
                    public boolean shouldExtract(ApplyScriptAfter toExtract) {
                        return (toExtract != null);
                    }
                });
    }

    public static SqlScriptProvider<ApplyScriptBefore> createProviderForScriptsToBeAppliedBeforeTest(TestClass testClass, ScriptingConfiguration configuration) {
        return SqlScriptProvider.forAnnotation(ApplyScriptBefore.class)
                .usingConfiguration(configuration)
                .extractingMetadataUsing(new MetadataExtractor(testClass))
                .namingFollows(new PrefixedScriptFileNamingStrategy("before-", "sql"))
                .build(new ValueExtractor<ApplyScriptBefore>() {
                    @Override
                    public String[] extract(ApplyScriptBefore toExtract) {
                        return toExtract.value();
                    }

                    @Override
                    public boolean shouldExtract(ApplyScriptBefore toExtract) {
                        return (toExtract != null);
                    }
                });
    }

    public static SqlScriptProvider<CreateSchema> createProviderForCreateSchemaScripts(TestClass testClass, ScriptingConfiguration configuration) {
        return SqlScriptProvider.forAnnotation(CreateSchema.class)
                .usingConfiguration(configuration)
                .extractingMetadataUsing(new MetadataExtractor(testClass))
                .namingFollows(new FileNamingStrategy<String>("sql") {
                    @Override
                    public String getFileExtension() {
                        return extension;
                    }
                })
                .build(new ValueExtractor<CreateSchema>() {
                    @Override
                    public String[] extract(CreateSchema toExtract) {
                        return toExtract.value();
                    }

                    @Override
                    public boolean shouldExtract(CreateSchema toExtract) {
                        return (toExtract != null);
                    }
                });
    }

    @Override
    protected SqlScriptResourceDescriptor createDescriptor(String resource) {
        if (!ScriptLoader.isSqlScriptFile(resource)) {
            return new InlineSqlScriptResourceDescriptor(resource);
        }

        return new FileSqlScriptResourceDescriptor(determineLocation(resource), configuration.getCharset());
    }

    @Override
    protected String defaultLocation() {
        return configuration.getDefaultSqlScriptLocation();
    }

    @Override
    protected String defaultFileName() {
        return strategy.createFileName(metadataExtractor.getJavaClass());
    }

    @Override
    public Collection<String> getResourceFileNames(Method testMethod) {
        final T annotation = getResourceAnnotation(testMethod);
        if (!extractor.shouldExtract(annotation)) {
            return Collections.emptyList();
        }

        if (filesNotSpecified(annotation)) {
            return Collections.singletonList(getDefaultFileName(testMethod));
        }

        return Arrays.asList(extractor.extract(annotation));
    }

    private boolean filesNotSpecified(T annotation) {
        final String[] specifiedFileNames = extractor.extract(annotation);
        return specifiedFileNames.length == 0 || "".equals(specifiedFileNames[0].trim());
    }

    private T getResourceAnnotation(Method testMethod) {
        return metadataExtractor.using(annotation).fetchUsingFirst(testMethod);
    }

    private String getDefaultFileName(Method testMethod) {

        if (metadataExtractor.using(annotation).isDefinedOn(testMethod)) {
            return strategy.createFileName(metadataExtractor.getJavaClass(), testMethod);
        }

        return strategy.createFileName(metadataExtractor.getJavaClass());
    }

}
