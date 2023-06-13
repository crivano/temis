package br.jus.trf2.temis.core.util;

import javax.persistence.EntityManager;

public interface SigaEntityManagerFactory {

	void addClass(Class clazz);

	void addProperty(String key, Object value);

	EntityManager getEntityManager();

	void addCache(String name, String strategy, int wakeUpInterval, int maxEntries, int lifespan, int maxIdle);

	void addProperties();

	void addClasses();

}