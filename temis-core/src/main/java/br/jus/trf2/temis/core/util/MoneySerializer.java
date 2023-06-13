package br.jus.trf2.temis.core.util;

//package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.persistence.EntityManagerFactory;

import org.joda.money.Money;

import com.crivano.jbiz.IEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import lombok.AllArgsConstructor;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class MoneySerializer implements JsonSerializer<Money> {

	@Inject
	protected EntityManagerFactory emf;

	public MoneySerializer() {
		// this.builder = builder;
	}

	@AllArgsConstructor
	private static class KeyOnly {
		Long key;
	}

	@Override
	public JsonElement serialize(Money src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getAmount());
	}

	private String deRef(String s) {
		if (s == null)
			return null;
		if (!s.startsWith("ref"))
			return "_" + s + "_FullSerialization";
		s = s.substring(3);
		return "_" + Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

}