package br.jus.trf2.temis.core.util;

import java.math.BigDecimal;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.converter.Converter;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@Convert(Money.class)
@ApplicationScoped
public class MoneyConverter implements Converter<Money> {

	public static Money converter(String value) {
		String s = Utils.sorn(value);
		if (s == null) {
			return null;
		}
		if (s.equals("undefined"))
			return null;
		return Money.of(CurrencyUnit.of("BRL"), BigDecimal.valueOf(Double.parseDouble(value)));
	}

	@Override
	public Money convert(String value, Class<? extends Money> type) {
		return converter(value);
	}

}