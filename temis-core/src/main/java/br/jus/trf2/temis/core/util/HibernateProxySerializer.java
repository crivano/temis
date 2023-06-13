package br.jus.trf2.temis.core.util;

import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import lombok.NoArgsConstructor;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
@NoArgsConstructor
public class HibernateProxySerializer implements JsonSerializer<HibernateProxy> {

	@Inject
	protected EntityManagerFactory emf;

	@Override
	public JsonElement serialize(HibernateProxy src, Type typeOfSrc, JsonSerializationContext context) {
		Object obj = Hibernate.unproxy(src);
		return context.serialize(obj);
	}
}