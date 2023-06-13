package br.jus.trf2.temis.core.util;

import java.util.ResourceBundle;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import org.joda.time.LocalDateTime;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.converter.Converter;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@Convert(LocalDateTime.class)
@ApplicationScoped
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

	@Override
	public LocalDateTime convert(String value, Class<? extends LocalDateTime> type) {
		String s = Utils.sorn(value);
		if (s == null) {
			return null;
		}
		if (s.equals("undefined"))
			return null;
		LocalDateTime r = LocalDateTime.parse(s);
		return r;
	}

}