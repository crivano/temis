package br.jus.trf2.temis.core.logic;

import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

public class EstaNao implements Expression {
	@Override
	public boolean eval() {
		return false;
	}

	@Override
	public String explain(boolean result) {
		return JLogic.explain("nunca está", result);
	}
};