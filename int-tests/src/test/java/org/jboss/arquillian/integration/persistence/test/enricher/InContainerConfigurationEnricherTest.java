package org.jboss.arquillian.integration.persistence.test.enricher;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/*
  This test is handled by dedicated Maven execution, as we want to run it against always fixed
  arquillian.xml configuration. For this purpose system property is used to point to the alternative configuration xml.
  Look in pom.xml for configuration-enricher execution of failsafe plugin. In order to run in from IDE set system variable
  -Darquillian.xml=arquillian-config-enricher-test.xml

  In order to enabled persistence extension we decorate this class with @PersistenceTest, as we don't use any other ways
  of triggering APE (@*DataSet annotations).
 */
@RunWith(Arquillian.class)
@PersistenceTest
public class InContainerConfigurationEnricherTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "dummy.jar")
            .addClass(org.jboss.arquillian.integration.persistence.test.enricher.InContainerConfigurationEnricherTest.class)
            .addPackages(true, "org.assertj.core")
            .addPackages(true,  "org.apache.tools")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("test-persistence.xml", "persistence.xml");
    }

    @ArquillianResource
    private PersistenceConfiguration persistenceConfiguration;

    @ArquillianResource
    private DBUnitConfiguration dbUnitConfiguration;

    @ArquillianResource
    private ScriptingConfiguration scriptingConfiguration;

    @Test
    public void should_be_able_to_access_persistence_configuration() throws Exception {
        assertThat(persistenceConfiguration.getDumpDirectory()).isEqualTo("TEST_DIR");
    }

    @Test
    public void should_be_able_to_access_dbunit_configuration() throws Exception {
        assertThat(dbUnitConfiguration.getDatatypeFactory().getClass().getCanonicalName())
            .isEqualTo("org.dbunit.ext.mckoi.MckoiDataTypeFactory");
    }

    @Test
    public void should_be_able_to_access_scripting_configuration() throws Exception {
        assertThat(scriptingConfiguration.getDefaultSqlScriptLocation())
            .isEqualTo("$HOME/scripts");
    }
}
