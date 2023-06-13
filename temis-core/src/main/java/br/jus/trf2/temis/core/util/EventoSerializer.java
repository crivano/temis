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

import com.crivano.jbiz.IEntity;
import com.crivano.jbiz.IEnum;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import br.jus.trf2.temis.core.Evento;
import lombok.AllArgsConstructor;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class EventoSerializer implements JsonSerializer<Evento> {

	@Inject
	protected EntityManagerFactory emf;

	public EventoSerializer() {
		// this.builder = builder;
	}

	@AllArgsConstructor
	private static class KeyOnly {
		Long key;
	}

	@Override
	public JsonElement serialize(Evento src, Type typeOfSrc, JsonSerializationContext context) {
		// src.beforeSerialize();

		JsonObject jsonObject = new JsonObject();
		try {
			jsonObject.addProperty("key", (Long) emf.getPersistenceUnitUtil().getIdentifier(src));
			jsonObject.addProperty("kind", src.getClass().getSimpleName());
			if (src.getTitulo() != null)
				jsonObject.addProperty("slug", Texto.slugify(src.getTitulo(), true, false));
		} catch (Exception e) {
		}
		Class clazz = src.getClass();
		while (true) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if ((field.getModifiers() & Modifier.STATIC) != 0)
					continue;
				if (field.getAnnotation(NoSerialization.class) != null)
					continue;
//				if (field.getAnnotation(AdministratorOnlySerialization.class) != null
//						&& !ContextInterceptor.isAdministrador())
//					continue;
				field.setAccessible(true);
				String name = field.getName();
				try {
					// jsonObject.add(name, gson.toJsonTree(field.get(src)));
					Object obj = field.get(src);
					if (obj != null && obj instanceof IEntity && field.getAnnotation(FullSerialization.class) == null) {
						jsonObject.add(name,
								context.serialize(new KeyOnly((Long) emf.getPersistenceUnitUtil().getIdentifier(obj))));
					} else if (obj != null && obj instanceof IEnum) {
						jsonObject.add(name, context.serialize(obj));
						jsonObject.add(name + "Descr", context.serialize(((IEnum) obj).getDescr()));
					} else
						jsonObject.add(name, context.serialize(obj));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			if (clazz == Object.class || clazz.getSuperclass() == Object.class)
				break;
			clazz = clazz.getSuperclass();
		}
		return jsonObject;
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