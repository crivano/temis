package br.jus.trf2.temis.core.util;

//package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import org.joda.time.LocalDate;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class LocalDateSerializer implements JsonSerializer<LocalDate> {

	public LocalDateSerializer() {
	}

	@Override
	public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
		String string = serializar(src);
		return context.serialize(string);
	}

	public static String serializar(LocalDate src) {
		String string = src.toString();
		return string;
	}
}