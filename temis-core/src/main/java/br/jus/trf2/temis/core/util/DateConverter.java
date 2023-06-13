package br.jus.trf2.temis.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.converter.Converter;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@Convert(Date.class)
@ApplicationScoped
public class DateConverter implements Converter<Date> {

	public static Date converter(String value) {
		String s = Utils.sorn(value);
		if (s == null) {
			return null;
		}
		if (s.equals("undefined"))
			return null;
		try {
			return getFormat().parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	protected static DateFormat getFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	}

	@Override
	public Date convert(String value, Class<? extends Date> type) {
		return converter(value);
	}

}