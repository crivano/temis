package br.jus.trf2.temis.core.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import br.com.caelum.vraptor.jpa.EntityManagerFactoryCreator;

public class TemisEntityManagerFactoryCreator extends EntityManagerFactoryCreator {

	@Inject
	private TemisEntityManagerFactory jemf;

	@ApplicationScoped
	@Specializes
	@Produces
	public EntityManagerFactory getEntityManagerFactory() {
		return jemf.getEntityManagerFactory();
	}

	public void destroy(@Disposes EntityManagerFactory factory) {
		if (factory.isOpen()) {
			factory.close();
		}
	}
}
