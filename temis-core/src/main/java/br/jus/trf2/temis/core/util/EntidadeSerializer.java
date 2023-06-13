package br.jus.trf2.temis.core.util;

//package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Hibernate;

import com.crivano.jbiz.IEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.IEntidade;
import lombok.AllArgsConstructor;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class EntidadeSerializer implements JsonSerializer<IEntidade> {

	@Inject
	protected EntityManagerFactory emf;

	public EntidadeSerializer() {
		// this.builder = builder;
	}

	@AllArgsConstructor
	private static class KeyOnly {
		Long key;
	}

	@Override
	public JsonElement serialize(IEntidade src, Type typeOfSrc, JsonSerializationContext context) {
		// src.beforeSerialize();
		if (src.getTermino() != null)
			return null;

		JsonObject jsonObject = new JsonObject();
		try {
			jsonObject.addProperty("key", (Long) emf.getPersistenceUnitUtil().getIdentifier(src));
			jsonObject.addProperty("title", src.getTitle());
			jsonObject.addProperty("firstLine", src.getSelectFirstLine());
			jsonObject.addProperty("secondLine", src.getSelectSecondLine());
			jsonObject.addProperty("descrCompleta", src.getDescrCompleta());
		} catch (Exception e) {
		}
		src = (IEntidade) Hibernate.unproxy(src);

		for (Field field : ModeloUtils.getFieldList(src.getClass())) {
			if (field.getAnnotation(NoSerialization.class) != null)
				continue;
//				if (field.getAnnotation(AdministratorOnlySerialization.class) != null
//						&& !ContextInterceptor.isAdministrador())
//					continue;
			String name = field.getName();

			// Skip hibernate proxy fields;
//				if (name.startsWith("$$"))
//					continue;
			try {
				// jsonObject.add(name, gson.toJsonTree(field.get(src)));
				Object obj = field.get(src);
				if (obj != null && obj instanceof IEntity && field.getAnnotation(FullSerialization.class) == null) {
					jsonObject.add(name,
							context.serialize(new KeyOnly((Long) emf.getPersistenceUnitUtil().getIdentifier(obj))));
				} else if (obj != null && obj instanceof Collection) {
					JsonArray a = new JsonArray();
					jsonObject.add(name, a);
					for (Object o : (Collection) obj) {
						if (o instanceof Entidade && ((Entidade) o).getTermino() != null)
							continue;
						a.add(context.serialize(o));
					}
				} else {
					obj = Hibernate.unproxy(obj);
					jsonObject.add(name, context.serialize(obj));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
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