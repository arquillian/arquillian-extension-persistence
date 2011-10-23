package org.jboss.arquillian.persistence.configuration;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.Manager;
import org.jboss.arquillian.core.spi.context.Context;
import org.jboss.arquillian.persistence.AbstractManagerTestBase;
import org.jboss.arquillian.persistence.util.ManagerFacade;
import org.jboss.arquillian.test.impl.context.SuiteContextImpl;
import org.jboss.arquillian.test.spi.context.SuiteContext;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationInitializationTest extends AbstractManagerTestBase {

	private ManagerFacade facade;

	@Override
	protected void addExtensions(List<Class<?>> extensions) {
		extensions.add(PersistenceConfigurationProducer.class);
	}

	@Override
	protected void addContexts(List<Class<? extends Context>> contexts) {
		contexts.add(SuiteContextImpl.class);
	}
	
	@Override
	protected void startContexts(Manager manager) {
		super.startContexts(manager);
		manager.getContext(SuiteContext.class).activate();
	}
	
	@Before
	public void initializeArquillianDescriptor() {
		facade = new ManagerFacade(getManager());
		bind(ApplicationScoped.class, ArquillianDescriptor.class, Descriptors.create(ArquillianDescriptor.class));
	}
	
	@Test
	public void shouldCreateConfigurationBeforeClassIsExecuted() throws Exception {
		// given
		BeforeClass beforeClassEvent = new BeforeClass(getClass());
		
		// when
		fire(beforeClassEvent);
		PersistenceConfiguration persistenceConfiguration = facade.getInstance(PersistenceConfiguration.class, SuiteContext.class);
		
		// then
		assertThat(persistenceConfiguration).isNotNull();
	}

}
