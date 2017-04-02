package org.jboss.arquillian.persistence.core.configuration;

import java.io.InputStream;
import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceDescriptorExtractorTest {

    PersistenceDescriptorExtractor persistenceDescriptorArchiveExtractor = new PersistenceDescriptorExtractor();

    PersistenceDescriptorParser persistenceDescriptorParser = new PersistenceDescriptorParser();

    @Test
    public void should_extract_persistence_descriptor_from_jar() throws Exception {
        // given
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClass(PersistenceDescriptorExtractorTest.class)
            .addAsManifestResource("persistence-jta.xml", "persistence.xml");
        // when
        InputStream inputStream = persistenceDescriptorArchiveExtractor.getAsStream(jar);
        String dataSourceName = persistenceDescriptorParser.obtainDataSourceName(inputStream);

        // then
        assertThat(dataSourceName).isEqualTo("java:app/datasources/postgresql_ds");
    }

    @Test
    public void should_extract_persistence_descriptor_from_war() throws Exception {
        // given
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackages(true, "org.assertj.core")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("persistence-non-jta.xml", "META-INF/persistence.xml");
        // when
        InputStream inputStream = persistenceDescriptorArchiveExtractor.getAsStream(war);
        String dataSourceName = persistenceDescriptorParser.obtainDataSourceName(inputStream);

        // then
        assertThat(dataSourceName).isEqualTo("java:app/datasources/postgresql_ds_non_jta");
    }

    @Test
    public void should_extract_persistence_descriptor_from_ear() throws Exception {
        // given
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClass(PersistenceDescriptorExtractorTest.class)
            .addAsManifestResource("persistence-jta.xml", "persistence.xml");
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
            .addAsLibrary(jar);
        // when
        InputStream inputStream = persistenceDescriptorArchiveExtractor.getAsStream(ear);
        String dataSourceName = persistenceDescriptorParser.obtainDataSourceName(inputStream);

        // then
        assertThat(dataSourceName).isEqualTo("java:app/datasources/postgresql_ds");
    }

    @Test
    public void should_return_null_when_ear_contains_multiple_sub_archives_with_persistence_xml() throws Exception {
        // given
        final JavaArchive jar1 = ShrinkWrap.create(JavaArchive.class, "test1.jar")
            .addClass(PersistenceDescriptorExtractorTest.class)
            .addAsManifestResource("persistence-jta.xml", "persistence.xml");

        final JavaArchive jar2 = ShrinkWrap.create(JavaArchive.class, "test2.jar")
            .addClass(PersistenceDescriptorExtractorTest.class)
            .addAsManifestResource("persistence-non-jta.xml", "persistence.xml");
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
            .addAsLibraries(jar1, jar2);
        // when
        InputStream inputStream = persistenceDescriptorArchiveExtractor.getAsStream(ear);

        // then
        assertThat(inputStream).isNull();
    }

    @Test
    public void should_return_extract_persistence_xml_from_testable_archive() throws Exception {
        // given
        final JavaArchive jar1 = ShrinkWrap.create(JavaArchive.class, "test1.jar")
            .addClass(PersistenceDescriptorExtractorTest.class)
            .addAsManifestResource("persistence-jta.xml", "persistence.xml");
        final JavaArchive jar2 = ShrinkWrap.create(JavaArchive.class, "test2.jar")
            .addClass(PersistenceDescriptorExtractorTest.class)
            .addAsManifestResource("persistence-non-jta.xml", "persistence.xml");
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
            .addAsLibraries(Testable.archiveToTest(jar1), jar2);
        // when
        InputStream inputStream = persistenceDescriptorArchiveExtractor.getAsStream(ear);
        String dataSourceName = persistenceDescriptorParser.obtainDataSourceName(inputStream);

        // then
        assertThat(dataSourceName).isEqualTo("java:app/datasources/postgresql_ds");
    }

    @Test
    public void should_return_null_if_no_persistence_xml_found() throws Exception {
        // given
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClass(PersistenceDescriptorExtractorTest.class);

        // when
        InputStream inputStream = persistenceDescriptorArchiveExtractor.getAsStream(jar);

        // then
        assertThat(inputStream).isNull();
    }

    @Test
    public void should_return_null_if_multiple_persistence_xml_files_found() throws Exception {
        // given
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClass(PersistenceDescriptorExtractorTest.class)
            .addAsManifestResource("persistence-jta.xml", "persistence.xml")
            .addAsManifestResource("persistence-jta.xml", "resource/persistence.xml");
        // when
        InputStream inputStream = persistenceDescriptorArchiveExtractor.getAsStream(jar);

        // then
        assertThat(inputStream).isNull();
    }
}
