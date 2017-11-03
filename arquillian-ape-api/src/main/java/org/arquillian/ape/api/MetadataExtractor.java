package org.arquillian.ape.api;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.jboss.arquillian.test.spi.TestClass;

public class MetadataExtractor {

    protected final TestClass testClass;

    protected final Map<Class<?>, AnnotationInspector<?>> inspectors = new HashMap<Class<?>, AnnotationInspector<?>>();

    public MetadataExtractor(TestClass testClass) {
        this.testClass = testClass;
    }

    public <K extends Annotation> void register(final TestClass testClass, final Class<K> annotation) {
        inspectors.put(annotation, new AnnotationInspector<K>(testClass, annotation));
    }

    @SuppressWarnings("unchecked")
    public <K extends Annotation> AnnotationInspector<K> using(final Class<K> annotation) {
        if (inspectors.get(annotation) == null) {
            register(testClass, annotation);
        }
        return (AnnotationInspector<K>) inspectors.get(annotation);
    }

    public AnnotationInspector<UsingDataSet> usingDataSet() {
        return using(UsingDataSet.class);
    }

    public AnnotationInspector<Cleanup> cleanup() {
        return using(Cleanup.class);
    }

    public AnnotationInspector<Server> server() {
        return using(Server.class);
    }

    public AnnotationInspector<Uri> uri() {
        return using(Uri.class);
    }

    public AnnotationInspector<Variable> variable() {
        return using(Variable.class);
    }

    public AnnotationInspector<UrlOverride> urlOverride() {
        return using(UrlOverride.class);
    }

    public AnnotationInspector<Authentication> authentication() {
        return using(Authentication.class);
    }

    public AnnotationInspector<Driver> driver() {
        return using(Driver.class);
    }

}
