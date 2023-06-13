package br.jus.trf2.temis.core.util;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import org.joda.time.LocalDate;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.converter.Converter;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@Convert(LocalDate.class)
@ApplicationScoped
public class LocalDateConverter implements Converter<LocalDate> {

	@Override
	public LocalDate convert(String value, Class<? extends LocalDate> type) {
		String s = Utils.sorn(value);
		if (s == null) {
			return null;
		}
		if (s.equals("undefined"))
			return null;
		LocalDate r = LocalDate.parse(s);
		return r;
	}

}