package br.jus.trf2.temis.core.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.beanutils.PropertyUtils;

import com.crivano.jbiz.IEntity;
import com.crivano.juia.annotations.Global;

import br.jus.trf2.temis.core.Entidade;

public class ModeloUtils {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Desconsiderar {
	}

	public static List<Class<?>> getClassHierarchy(Class<?> baseClass) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (Class<?> clazz = baseClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
			classes.add(0, clazz);
		}
		return classes;
	}

	public static List<Field> getFieldList(Class<?> baseClass) {
		List<Field> l = new ArrayList<>();
		for (Class<?> clazz : getClassHierarchy(baseClass)) {
			Field fieldlist[] = clazz.getDeclaredFields();
			for (int i = 0; i < fieldlist.length; i++) {
				Field fld = fieldlist[i];
				fld.setAccessible(true);
				l.add(fld);
			}
		}
		return l;
	}

	public static void copyProperties(Object dest, Object orig, Set<String> exceto) {
		try {
			Map<String, Field> fldsOrig = new HashMap<>();
			for (Field fld : getFieldList(orig.getClass())) {
				fldsOrig.put(fld.getName(), fld);
			}
			for (Field fld : getFieldList(dest.getClass())) {
				Field fldOrig = fldsOrig.get(fld.getName());
				if (fldOrig == null)
					continue;
				OneToMany o2m = fld.getAnnotation(OneToMany.class);
				if (o2m == null || Utils.sorn(o2m.mappedBy()) == null) {
					// propriedade simples
					if (exceto == null || !exceto.contains(fld.getName()))
						fld.set(dest, fldOrig.get(orig));
					continue;
				} else {
					// lista marcada com @OneToMany
					Collection<IEntity> lOrig = (Collection<IEntity>) fldOrig.get(orig);
					Collection<IEntity> lDest = (Collection<IEntity>) fld.get(dest);

					Collection<IEntity> lTemp = new ArrayList<>();
					lTemp.addAll(lDest);
					lDest.clear();
					if (lOrig != null) {
						for (IEntity oOrig : lOrig) {
							boolean encontrado = false;
							// remover itens de destino que não existem na origem,
							// atualizar itens que existem nos dois e
							for (IEntity oDest : lTemp) {
								Long idOrig = ContextInterceptor.getDao().getIdentifier(oOrig);
								Long idDest = ContextInterceptor.getDao().getIdentifier(oDest);
								if (idDest.equals(idOrig)) {
									Set<String> set = new HashSet<>();
									set.add(o2m.mappedBy());
									copyProperties(oDest, oOrig, set);
									lDest.add(oDest);
									encontrado = true;
									break;
								}
							}
							if (!encontrado) {
								// inserir itens que só existem na origem
								// Faz o link reverso preenchendo o campo indicado por mappedBy()
								for (Field fldObj : ModeloUtils.getFieldList(oOrig.getClass())) {
									if (o2m.mappedBy().equals(fldObj.getName())) {
										fldObj.set(oOrig, dest);
									}
								}
								lDest.add(oOrig);
							}
						}
					}
					// Atribui o campo de ordenação, começando por 1
					int i = 0;
					for (IEntity oDest : lDest) {
						i++;
						Global juiaGlobal = oDest.getClass().getAnnotation(Global.class);
						if (juiaGlobal != null && juiaGlobal.sortField().length() > 0)
							PropertyUtils.setProperty(oDest, juiaGlobal.sortField(), i);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean compareProperties(Object dest, Object orig, Set<String> exceto) {
		try {
			Map<String, Field> fldsOrig = new HashMap<>();
			for (Field fld : getFieldList(orig.getClass())) {
				fldsOrig.put(fld.getName(), fld);
			}
			for (Field fld : getFieldList(dest.getClass())) {
				Field fldOrig = fldsOrig.get(fld.getName());
				if (fldOrig == null)
					continue;
				OneToMany o2m = fld.getAnnotation(OneToMany.class);
				if (o2m != null && Utils.sorn(o2m.mappedBy()) != null)
					continue;
				ManyToOne m2o = fld.getAnnotation(ManyToOne.class);
				if (m2o != null)
					continue;
				// propriedade simples
				if (exceto != null && exceto.contains(fld.getName()))
					continue;
				Object o;
				o = fldOrig.get(orig);
				Object d = fld.get(dest);
				if (o == null && d == null)
					continue;
				if (o.equals(d))
					continue;
				return false;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public static void initProperties(IEntity data) {
		try {
			for (Field fld : getFieldList(data.getClass())) {
				OneToMany o2m = fld.getAnnotation(OneToMany.class);
				if (o2m == null || Utils.sorn(o2m.mappedBy()) == null) {
					continue;
				} else {
					// lista marcada com @OneToMany
					Collection<IEntity> l = (Collection<IEntity>) fld.get(data);
					if (l != null) {
						int i = 0;
						for (IEntity oOrig : l) {
							// Remove a ID da entidade na lista para forçar a regravação
							if (oOrig instanceof Entidade)
								((Entidade) oOrig).setId(null);
							// Atribui o campo de ordenação, começando por 1
							i++;
							Global juiaGlobal = oOrig.getClass().getAnnotation(Global.class);
							if (juiaGlobal != null && juiaGlobal.sortField().length() > 0)
								PropertyUtils.setProperty(oOrig, juiaGlobal.sortField(), i);
							// Faz o link reverso preenchendo o campo indicado por mappedBy()
							for (Field fldObj : ModeloUtils.getFieldList(oOrig.getClass())) {
								if (o2m.mappedBy().equals(fldObj.getName())) {
									fldObj.set(oOrig, data);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadProperties(IEntity data) throws Exception {
		for (Field fld : getFieldList(data.getClass())) {
			OneToMany o2m = fld.getAnnotation(OneToMany.class);
			if (o2m != null && Utils.sorn(o2m.mappedBy()) != null && List.class.isAssignableFrom(fld.getType())) {
				// lista marcada com @OneToMany
				List<IEntity> l = (List<IEntity>) fld.get(data);

				// Ordena a lista em função da propriedade encontrada em @Global.sortField()
				Collections.sort(l, new Comparator<IEntity>() {

					@Override
					public int compare(IEntity o1, IEntity o2) {
						try {
							Integer oo1 = ordem(o1);
							Integer oo2 = ordem(o2);
							if (oo1 == null) {
								if (oo2 == null)
									return 0;
								else
									return -oo2.compareTo(oo1);
							}
							return oo1.compareTo(oo2);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}

					private Integer ordem(IEntity o)
							throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
						Global juiaGlobal = o.getClass().getAnnotation(Global.class);
						if (juiaGlobal != null && juiaGlobal.sortField().length() > 0)
							return (Integer) PropertyUtils.getProperty(o, juiaGlobal.sortField());
						return null;
					}
				});
			}
		}
	}

	public static <T> boolean semelhante(T s, T obj) throws Exception {
		Class cls = s.getClass();

		if (obj == null)
			return false;
		do {
			Field fieldlist[] = cls.getDeclaredFields();
			for (int i = 0; i < fieldlist.length; i++) {
				Field fld = fieldlist[i];
				fld.setAccessible(true);
				if (((fld.getModifiers() & Modifier.STATIC) != 0) || fld.isAnnotationPresent(Desconsiderar.class))
					continue;

				Object o1 = fld.get(s);
				Object o2 = fld.get(obj);

				if (o1 == null) {
					if (o2 != null)
						return false;
				} else {
					if (o2 == null)
						return false;
					if (!o1.equals(o2))
						return false;
				}
			}
			cls = cls.getSuperclass();
		} while (!cls.equals(Object.class));
		return true;
	}

}
